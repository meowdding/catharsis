package me.owdding.katharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB

@GenerateCodec
data class PositionEntityCondition(
    val minX: Double,
    val minY: Double,
    val minZ: Double,
    val maxX: Double,
    val maxY: Double,
    val maxZ: Double,
) : EntityCondition {
    override val codec: MapCodec<out EntityCondition> get() = KatharsisCodecs.getMapCodec<PositionEntityCondition>()
    private val aabb = AABB(minX, minY, minZ, maxX, maxY, maxZ)

    override fun matches(entity: Entity) = aabb.contains(entity.position())

}
