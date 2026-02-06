package me.owdding.catharsis.features.blocks.replacements.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property

@GenerateCodec
data class PropertiesCondition(
    @NamedCodec("blockstate_properties") val properties: Map<String, String>
): BlockCondition {

    override val codec: MapCodec<out BlockCondition> = CatharsisCodecs.getMapCodec<PropertiesCondition>()

    override fun check(state: BlockState, pos: BlockPos, level: BlockAndTintGetter, random: RandomSource): Boolean {
        for ((key, value) in properties) {
           if (state.block.stateDefinition.getProperty(key)?.check(state, value) != true) {
               return false
           }
        }
        return true
    }

    private fun <T : Comparable<T>> Property<T>.check(state: BlockState, value: String): Boolean {
        return state.hasProperty(this) && this.getName(state.getValue(this)) == value
    }
}
