package me.owdding.catharsis.features.blocks

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.extensions.mapBothNotNull
import me.owdding.catharsis.utils.types.fabric.PreparingModelLoadingPlugin
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.level.block.Block
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlin.jvm.optionals.getOrNull

@Module
object BlockReplacements : PreparingModelLoadingPlugin<Map<Block, LayeredBlockReplacements>> {
    init {
        register()
    }

    private val logger = Catharsis.featureLogger("BlockReplacements")
    private val blockReplacementConverter = FileToIdConverter.json("catharsis/block_replacements")
    private val blockStateConverter = FileToIdConverter.json("catharsis/virtual_block_states")
    private val gson = GsonBuilder().create()

    private val blockDefinitionCodec: Codec<BlockReplacement.Completable> = BlockStateDefinitions.CODEC.codec()
    private val virtualBlockStateCodec: Codec<VirtualBlockStateDefinition> = CatharsisCodecs.VirtualBlockStateDefinitionCodec.codec()

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

    fun loadBlockReplacements(resourceManager: ResourceManager): Map<ResourceLocation, LayeredBlockReplacements.Completable> {
        return blockReplacementConverter.listMatchingResourceStacks(resourceManager).mapBothNotNull { (id, value) ->
            val replacements = LayeredBlockReplacements.Completable(
                value.mapNotNull {
                    logger.runCatching("Error loading block replacement definition $id") {
                        it.openAsReader().use { reader ->
                            gson.fromJson(reader, JsonElement::class.java).toDataOrThrow(blockDefinitionCodec)
                        }
                    }
                },
            ).takeUnless { it.definitions.isEmpty() }
            blockReplacementConverter.fileToId(id) to replacements
        }
    }

    fun loadBlockStates(resourceManager: ResourceManager, map: Map<ResourceLocation, LayeredBlockReplacements.Completable>): Map<Block, LayeredBlockReplacements> {
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
        context.modifyBlockModelOnLoad().register { original, context ->
            val block = context.state().block

            data[block]?.let {
                return@register UnbakedBlockStateModelReplacement(block, original, it)
            }

            original
        }
    }
}

data class BlockReplacementBakery(
    val virtualStates: Map<ResourceLocation, VirtualBlockStateDefinition>
)
