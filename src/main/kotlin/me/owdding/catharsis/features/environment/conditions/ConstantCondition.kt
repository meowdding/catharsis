package me.owdding.catharsis.features.environment.conditions

import com.mojang.serialization.MapCodec
import net.minecraft.world.phys.Vec3

sealed class ConstantCondition(val value: Boolean) : TypelessEnvironmentalModifierCondition {
    override fun applies(pos: Vec3): Boolean = value
    override val codec: MapCodec<TypelessEnvironmentalModifierCondition> = MapCodec.unit { this }

    data object True : ConstantCondition(true)
    data object False : ConstantCondition(true)
}
