package me.owdding.catharsis.features.armor

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.armor.models.ArmorModel
import me.owdding.catharsis.features.armor.models.ArmorModelState
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.*

data class ArmorDefinition(
    val model: ArmorModel,
    val partVisibility: EnumMap<BodyPart, PartVisibilityState>,
) {

    fun resolve(stack: ItemStack, entity: LivingEntity?, slot: EquipmentSlot): ArmorModelState {
        return model.resolve(stack, McClient.self.level, entity, slot.ordinal + (entity?.id ?: 0))
    }

    @GenerateCodec
    data class Unbaked(
        val model: ArmorModel.Unbaked,
        @FieldName("part_visibility") val partVisibility: EnumMap<BodyPart, PartVisibilityState> = EnumMap(BodyPart::class.java),
    ) {

        fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorDefinition {
            return ArmorDefinition(model.bake(swapper, resources), partVisibility)
        }
    }
}


data class PartVisibilityState(
    val overlay: Boolean = true,
    val base: Boolean = true,
) {
    companion object {
        @IncludedCodec
        val CODEC: Codec<PartVisibilityState> = Codec.withAlternative(
            RecordCodecBuilder.create {
                it.group(
                    Codec.BOOL.optionalFieldOf("overlay", true).forGetter(PartVisibilityState::overlay),
                    Codec.BOOL.optionalFieldOf("base", true).forGetter(PartVisibilityState::base),
                ).apply(it, ::PartVisibilityState)
            },
            Codec.BOOL.xmap({ PartVisibilityState(it, it) }, { it.overlay && it.base }),
        )

        @JvmField
        val DEFAULT = PartVisibilityState(overlay = true, base = true)
    }
}

enum class BodyPart {
    LEFT_ARM,
    RIGHT_ARM,
    CAPE,
    HEAD,
    CHEST,
    LEFT_LEG,
    RIGHT_LEG,
}
