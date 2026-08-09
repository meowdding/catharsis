package me.owdding.catharsis.features.environment.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.utils.extensions.unsafeCast
import net.minecraft.world.phys.Vec3

data class EnvironmentalAndCondition<Value : Any>(val conditions: List<EnvironmentalModifierCondition<Value>>) : TypelessEnvironmentalModifierCondition {
    constructor(vararg conditions: EnvironmentalModifierCondition<Value>) : this(conditions.toList())

    override val codec: MapCodec<out TypelessEnvironmentalModifierCondition> get() = TODO()

    override fun applies(pos: Vec3): Boolean = conditions.all { it is TypelessEnvironmentalModifierCondition && it.applies(pos) }
    override fun applies(baseValue: Any, pos: Vec3): Boolean = conditions.all { it.applies(baseValue.unsafeCast(), pos) }
}
