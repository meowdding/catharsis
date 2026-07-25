package me.owdding.katharsis.features.blocks

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.features.blocks.replacements.*
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.codecs.IncludedCodecs
import me.owdding.katharsis.utils.codecs.SavableData
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

interface BlockReplacement {
    interface Completable : SavableData<Completable> {
        override val codec: Codec<Completable> get() = BlockReplacements.blockDefinitionCodec
        override fun toFileName(identifier: Identifier): Identifier = BlockReplacements.blockReplacementConverter.idToFile(identifier)

        fun codec(): MapCodec<out Completable>

        fun virtualStates(): List<Identifier>
        fun bake(bakery: BlockReplacementBakery): BlockReplacement
    }

    fun listStates(): List<VirtualBlockStateDefinition>
    fun <T : Any> bake(baker: BlockReplacement.() -> BlockReplacementSelector<T>): BlockReplacementSelector<T>
    fun bakeModel(baker: ModelBaker, block: Block): BlockReplacementSelector<BlockReplacementEntry> = bake { bakeModel(baker, block) }
    fun bakeSounds(block: Block): BlockReplacementSelector<BlockSoundDefinition> = bake { bakeSounds(block) }
    fun bakeDisplay(block: Block): BlockReplacementSelector<BlockDisplayDefinition> = bake { bakeDisplay(block) }

    fun select(
        level: BlockAndTintGetter?,
        state: BlockState,
        pos: BlockPos,
        random: RandomSource,
    ): VirtualBlockStateDefinition?
}

object BlockStateDefinitions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out BlockReplacement.Completable>>()

    @IncludedCodec
    val CODEC: MapCodec<BlockReplacement.Completable> = ID_MAPPER.codec(IncludedCodecs.katharsisIdentifier).dispatchMap(BlockReplacement.Completable::codec) { it }

    init {
        ID_MAPPER.put(Katharsis.id("select"), KatharsisCodecs.getMapCodec<SelectBlockReplacement.Completable>())
        ID_MAPPER.put(Katharsis.id("redirect"), KatharsisCodecs.getMapCodec<RedirectBlockReplacement.Completable>())
        ID_MAPPER.put(Katharsis.id("per_area"), KatharsisCodecs.getMapCodec<PerAreaBlockReplacement.Completable>())
        ID_MAPPER.put(Katharsis.id("random"), KatharsisCodecs.getMapCodec<RandomBlockReplacement.Completable>())
        ID_MAPPER.put(Katharsis.id("conditional"), KatharsisCodecs.getMapCodec<ConditionalBlockReplacement.Completable>())
        ID_MAPPER.put(Katharsis.id("layered"), KatharsisCodecs.getMapCodec<LayeredBlockReplacements.Completable>())
    }
}
