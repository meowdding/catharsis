package me.owdding.catharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.types.FloatPredicate
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import kotlin.jvm.optionals.getOrDefault

@GenerateCodec
data class NbtNumberEntityCondition(
    val key: String,
    @FieldNames("values", "value") @Compact val values: FloatPredicate,
) : EntityCondition {

    override val codec: MapCodec<out EntityCondition> = CatharsisCodecs.getMapCodec<NbtNumberEntityCondition>()

    override fun matches(entity: EntityCheck): Boolean {
        val entityNbt = entity.data
        if (!entityNbt.contains(key)) return false
        return values.contains(entityNbt.getFloat(key).getOrDefault(0f))
    }
}
