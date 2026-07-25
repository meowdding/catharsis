package me.owdding.katharsis.features.entity.conditions

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.types.FloatPredicate
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

    override val codec: MapCodec<out EntityCondition> = KatharsisCodecs.getMapCodec<NbtNumberEntityCondition>()

    override fun matches(entity: Entity): Boolean {
        val entityNbt = entity.save()
        if (!entityNbt.contains(key)) return false
        return values.contains(entityNbt.getFloat(key).getOrDefault(0f))
    }
}
