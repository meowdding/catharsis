package me.owdding.katharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import java.util.*

@GenerateCodec
data class IdentityEntityCondition(
    val uuid: UUID?,
    val name: String?
) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = KatharsisCodecs.getMapCodec<IdentityEntityCondition>()
    override val cost: Int = if (name != null) 2 else super.cost

    override fun matches(entity: Entity): Boolean {
        if (uuid != null && entity.uuid != uuid) return false
        if (name != null && entity.cleanName != name) return false

        return true
    }
}
