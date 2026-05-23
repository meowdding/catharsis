package me.owdding.catharsis.features.blocks.skyblock

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.blocks.replacements.conditions.BlockCondition
import net.minecraft.world.level.block.Block

interface SkyBlockBlock : BlockCondition {
    val id: String
    val vanillaBlocks: List<Block>

    override val codec: MapCodec<out BlockCondition> get() = throw UnsupportedOperationException("meow todo")
}

object SkyBlockBlockRegistry {
    val REGISTRY = mutableMapOf<String, SkyBlockBlock>()

    init {
        register(GlowingMushroom)
    }

    private fun register(block: SkyBlockBlock) {
        REGISTRY[block.id] = block
    }
}
