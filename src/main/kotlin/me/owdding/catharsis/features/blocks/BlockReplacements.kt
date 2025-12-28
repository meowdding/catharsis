package me.owdding.catharsis.features.blocks

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.google.common.collect.MultimapBuilder
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.blocks.replacements.LayeredBlockReplacements
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.CatharsisLogger.Companion.featureLogger
import me.owdding.catharsis.utils.extensions.mapBothNotNull
import me.owdding.catharsis.utils.types.fabric.PreparingModelLoadingPlugin
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.BlockChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.extentions.filterValuesNotNull
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

@Module
object BlockReplacements : PreparingModelLoadingPlugin<Map<Block, LayeredBlockReplacements>>, CatharsisLogger by Catharsis.featureLogger() {
    init {
        register()
    }

    private val logger: CatharsisLogger = this
    private val blockReplacementConverter = FileToIdConverter.json("catharsis/block_replacements")
    private val blockStateConverter = FileToIdConverter.json("catharsis/virtual_block_states")
    private val gson = GsonBuilder().create()

    private val blockDefinitionCodec: Codec<BlockReplacement.Completable> = BlockStateDefinitions.CODEC.codec()
    private val virtualBlockStateCodec: Codec<VirtualBlockStateDefinition> = CatharsisCodecs.VirtualBlockStateDefinitionCodec.codec()

    private val map: MutableMap<Block, BakedSoundDefinition> = mutableMapOf()

    val blocksCache: Cache<BlockPos, BlockState> = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(5.minutes.toJavaDuration()).build<BlockPos, BlockState>()
    private val blockToListen: MutableSet<Block> = mutableSetOf()

    @JvmStatic
    fun getSound(state: BlockState, pos: BlockPos): BlockSoundDefinition = map[state.block]?.select(state, pos) ?: BlockSoundDefinition.DEFAULT

    @Subscription
    fun onBlockChange(event: BlockChangeEvent) {
        if (!McLevel.hasLevel) return

        if (event.state.block in blockToListen) {
            val oldState = McLevel[event.pos]
            if (event.state != oldState) {
                blocksCache.put(event.pos, oldState)
            }
        }
    }

    override fun prepare(
        resourceManager: ResourceManager,
        executor: Executor,
    ): CompletableFuture<Map<Block, LayeredBlockReplacements>> {
        return CompletableFuture.supplyAsync(
            { loadBlockReplacements(resourceManager) },
            executor,
        ).thenApplyAsync(
            { map -> loadBlockStates(resourceManager, map) },
            executor,
        ).whenComplete { _, throwable ->
            if (throwable != null) {
                Catharsis.error("Failed to prepare block replacements!", throwable)
            }
        }
    }

    fun loadBlockReplacements(resourceManager: ResourceManager): Map<Identifier, LayeredBlockReplacements.Completable> {
        val multiMap = MultimapBuilder.hashKeys().arrayListValues().build<Identifier, BlockReplacement.Completable>()
        blockReplacementConverter.listMatchingResourceStacks(resourceManager).forEach { (id, value) ->
            val replacements = value.mapNotNull {
                logger.runCatching("Error loading block replacement definition $id") {
                    it.openAsReader().use { reader ->
                        gson.fromJson(reader, JsonElement::class.java).toDataOrThrow(blockDefinitionCodec)
                    }
                }
            }
            multiMap.putAll(
                blockReplacementConverter.fileToId(id).let {
                    if (it.path.contains('/')) {
                        return@let Identifiers.of(it.path.substringBefore("/"), it.path.substringAfter("/"))
                    }

                    logger.warn("Found old block replacement ($id), consider migrating to new format")
                    it
                },
                replacements,
            )
        }

        return multiMap.asMap().mapValues { (_, value) -> LayeredBlockReplacements.Completable(value.toList()) }
    }

    fun loadBlockStates(resourceManager: ResourceManager, map: Map<Identifier, LayeredBlockReplacements.Completable>): Map<Block, LayeredBlockReplacements> {
        val entries = blockStateConverter.listMatchingResources(resourceManager).mapNotNull { (id, resource) ->
            logger.runCatching("Error loading virtual block state $id") {
                resource.openAsReader().use { reader ->
                    blockStateConverter.fileToId(id) to gson.fromJson(reader, JsonElement::class.java).toDataOrThrow(virtualBlockStateCodec)
                }
            }
        }.toMap()

        val bakery = BlockReplacementBakery(entries)
        return map.mapBothNotNull { (id, value) ->
            BuiltInRegistries.BLOCK.getOptional(id).getOrNull() to value.complete(bakery, logger).takeUnless { it.definitions.isEmpty() }
        }
    }

    override fun initialize(
        data: Map<Block, LayeredBlockReplacements>,
        context: ModelLoadingPlugin.Context,
    ) {
        this.map.clear()
        blockToListen.clear()
        val overrides = data.entries.flatMap { (block, replacement) ->
            replacement.listStates().flatMap { state -> state.overrides.keys.map { it to block } }
        }.groupBy({ (block) -> block }, { (_, block) -> block })
        blockToListen.addAll(overrides.keys)


        val emptyReplacement = LayeredBlockReplacements(emptyList())
        context.modifyBlockModelOnLoad().register { original, context ->
            val block = context.state().block

            val replacement = data[block]
            val tot = overrides.getOrElse(block) { emptyList() }.associateWith { data[it] }.filterValuesNotNull()
            if (replacement != null || tot.isNotEmpty()) {
                this.map[block] = BakedSoundDefinition(
                    (replacement ?: emptyReplacement).bakeSounds(block),
                    tot.mapValues { (_, value) -> value.bakeSounds(block) },
                )
                return@register UnbakedBlockStateModelReplacement(block, original, replacement ?: emptyReplacement, tot)
            }

            original
        }
    }
}

data class BlockReplacementBakery(
    val virtualStates: Map<Identifier, VirtualBlockStateDefinition>,
)
