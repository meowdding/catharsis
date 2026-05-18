package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.types.FloatPredicate
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.platform.save
import kotlin.jvm.optionals.getOrDefault

@GenerateCodec
data class NbtNumberEntityCondition(
    val key: String,
    @FieldNames("values", "value") @Compact val values: FloatPredicate,
) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<NbtNumberEntityCondition>()

    override fun matches(entity: Entity): Boolean {
        val entityNbt = entity.save()
        if (!entityNbt.contains(key)) return false
        return values.contains(entityNbt.getFloat(key).getOrDefault(0f))
    }
}
