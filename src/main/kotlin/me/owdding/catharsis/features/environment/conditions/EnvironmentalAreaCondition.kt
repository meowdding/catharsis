package me.owdding.catharsis.features.environment.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.area.Areas
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.extensions.toBlockPos
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.resources.Identifier
import net.minecraft.world.phys.Vec3

@GenerateCodec
data class EnvironmentalAreaCondition(
    val area: Identifier,
) : TypelessEnvironmentalModifierCondition {
    override val codec: MapCodec<out TypelessEnvironmentalModifierCondition> = CatharsisCodecs.EnvironmentalAreaConditionCodec

    override fun applies(pos: Vec3): Boolean {
        return Areas.isInArea(pos.toBlockPos(), area)
    }

}
