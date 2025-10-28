package me.owdding.catharsis.features.blocks

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.area.Areas
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktcodecs.NamedCodec
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadTransform
import net.minecraft.client.renderer.block.model.BlockStateModel
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

interface BlockReplacement {
    interface Completable {
        val codec: MapCodec<out Completable>

        fun virtualStates(): List<ResourceLocation>
        fun bake(bakery: BlockReplacementBakery): BlockReplacement
    }

    fun listStates(): List<VirtualBlockStateDefinition>
    fun bake(baker: ModelBaker, block: Block): BlockReplacementSelector
}

data class LayeredBlockReplacements(
    val definitions: List<BlockReplacement>,
) {
    fun listStates(): List<VirtualBlockStateDefinition> = definitions.flatMap { it.listStates() }
    data class Completable(
        val definitions: List<BlockReplacement.Completable>,
    ) {
        fun complete(bakery: BlockReplacementBakery, logger: CatharsisLogger): LayeredBlockReplacements = LayeredBlockReplacements(
            definitions.mapNotNull {
                logger.runCatching("Failed to bake block replacement $it") {
                    it.bake(bakery)
                }
            },
        )
    }

    data class LayeredBlockReplacementSelector(
        val blockReplacementSelectors: List<BlockReplacementSelector>,
    ) : BlockReplacementSelector {
        override fun select(
            state: BlockState,
            pos: BlockPos,
            random: RandomSource,
        ): BlockReplacementEntry? = blockReplacementSelectors.firstNotNullOfOrNull { it.select(state, pos, random) }
    }

    fun bake(baker: ModelBaker, block: Block): BlockReplacementSelector = LayeredBlockReplacementSelector(definitions.map { it.bake(baker, block) })
}


data class RedirectBlockReplacement(
    val virtualState: VirtualBlockStateDefinition,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = listOf(virtualState)
    override fun bake(baker: ModelBaker, block: Block) = BlockReplacementSelector.always(Baked(virtualState.blend, virtualState.instantiate(block, baker)))

    data class Baked(
        override val blend: BlendMode?,
        override val models: Map<BlockState, BlockStateModel>,
    ) : BlockReplacementEntry {
        override val transform: QuadTransform by lazy {
            if (blend != null) {
                QuadTransform { quad ->
                    quad.renderLayer(blend.toSectionLayer())
                    true
                }
            } else {
                QuadTransform { true }
            }
        }
    }

    @GenerateCodec
    @NamedCodec("CompletableRedirectBlockReplacement")
    data class Completable(
            @FieldName("virtual_state") val virtualState: ResourceLocation,
    ) : BlockReplacement.Completable {
        override val codec: MapCodec<Completable> = CatharsisCodecs.getMapCodec()
        override fun virtualStates() = listOf(virtualState)
        override fun bake(bakery: BlockReplacementBakery): BlockReplacement = RedirectBlockReplacement(bakery.virtualStates[virtualState]!!)
    }
}


data class PerAreaBlockReplacement(
    val values: Map<ResourceLocation, BlockReplacement>,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = values.values.flatMap { it.listStates() }
    data class PerAreaBlockReplacementSelector(
        val values: Map<ResourceLocation, BlockReplacementSelector>,
    ) : BlockReplacementSelector {
        override fun select(
            state: BlockState,
            pos: BlockPos,
            random: RandomSource,
        ): BlockReplacementEntry? {
            return values.firstNotNullOfOrNull { (area, value) ->
                value.takeIf { Areas.getLoadedAreas()[area]?.contains(pos) == true }?.select(state, pos, random)
            }
        }

    }

    override fun bake(
        baker: ModelBaker,
        block: Block,
    ): BlockReplacementSelector = PerAreaBlockReplacementSelector(values.mapValues { (_, value) -> value.bake(baker, block) })

    @GenerateCodec
    @NamedCodec("CompletablePerAreaBlockReplacement")
    data class Completable(
        @FieldName("values") val values: Map<ResourceLocation, BlockReplacement.Completable>,
    ) : BlockReplacement.Completable {
        override val codec: MapCodec<Completable> = CatharsisCodecs.getMapCodec()
        override fun virtualStates() = values.values.flatMap { it.virtualStates() }
        override fun bake(bakery: BlockReplacementBakery): BlockReplacement = PerAreaBlockReplacement(values.mapValues { it.value.bake(bakery) })
    }
}


data class RandomBlockReplacement(
    val min: Float,
    val max: Float,
    val threshold: Float,
    val definition: BlockReplacement,
    val fallback: BlockReplacement?,
) : BlockReplacement {
    override fun listStates(): List<VirtualBlockStateDefinition> = listOfNotNull(definition.listStates(), fallback?.listStates()).flatten()
    data class RandomBlockReplacementSelector(
        val min: Float, val max: Float, val threshold: Float,
        val definition: BlockReplacementSelector,
        val fallback: BlockReplacementSelector?,
    ) : BlockReplacementSelector {
        override fun select(
            state: BlockState,
            pos: BlockPos,
            random: RandomSource,
        ): BlockReplacementEntry? {
            return if (min + random.nextFloat() * (max - min) >= threshold) {
                definition
            } else {
                fallback
            }?.select(state, pos, random)
        }
    }

    override fun bake(
        baker: ModelBaker,
        block: Block,
    ): BlockReplacementSelector = RandomBlockReplacementSelector(min, max, threshold, definition.bake(baker, block), fallback?.bake(baker, block))

    @GenerateCodec
    @NamedCodec("CompletableRandomBlockReplacement")
    data class Completable(
        val min: Float,
        val max: Float,
        val threshold: Float,
        val definition: BlockReplacement.Completable,
        val fallback: BlockReplacement.Completable?,
    ) : BlockReplacement.Completable {
        override val codec: MapCodec<Completable> = CatharsisCodecs.getMapCodec()
        override fun virtualStates() = listOfNotNull(definition.virtualStates(), fallback?.virtualStates()).flatten()
        override fun bake(bakery: BlockReplacementBakery) = RandomBlockReplacement(
            min, max, threshold,
            definition.bake(bakery), fallback?.bake(bakery),
        )
    }
}

object BlockStateDefinitions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out BlockReplacement.Completable>>()

    @IncludedCodec
    val CODEC: MapCodec<BlockReplacement.Completable> = ID_MAPPER.codec(IncludedCodecs.catharsisResourceLocation).dispatchMap(BlockReplacement.Completable::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("redirect"), CatharsisCodecs.getMapCodec<RedirectBlockReplacement.Completable>())
        ID_MAPPER.put(Catharsis.id("per_area"), CatharsisCodecs.getMapCodec<PerAreaBlockReplacement.Completable>())
        ID_MAPPER.put(Catharsis.id("random"), CatharsisCodecs.getMapCodec<RandomBlockReplacement.Completable>())
    }
}
