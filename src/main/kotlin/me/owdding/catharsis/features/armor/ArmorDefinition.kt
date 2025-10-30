package me.owdding.catharsis.features.armor

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.features.armor.models.ArmorModel
import me.owdding.catharsis.features.armor.models.ArmorModelState
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.util.*

data class ArmorDefinition(
    val model: ArmorModel,
    val hiddenBodyParts: EnumMap<BodyPart, HiddenState>,
) {

    fun resolve(stack: ItemStack, entity: LivingEntity?, slot: EquipmentSlot): ArmorModelState {
        return model.resolve(stack, McClient.self.level, entity, slot.ordinal + (entity?.id ?: 0))
    }

    @GenerateCodec
    data class Unbaked(
        val model: ArmorModel.Unbaked,
        @FieldName("hidden_body_parts") val hiddenBodyParts: EnumMap<BodyPart, HiddenState> = EnumMap(BodyPart::class.java),
    ) {

        fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorDefinition {
            return ArmorDefinition(model.bake(swapper, resources), hiddenBodyParts)
        }
    }
}


data class HiddenState(
    val overlay: Boolean,
    val base: Boolean,
) {
    companion object {
        @IncludedCodec
        val CODEC: Codec<HiddenState> = Codec.withAlternative(
            RecordCodecBuilder.create {
                it.group(
                    Codec.BOOL.fieldOf("base").forGetter(HiddenState::base),
                    Codec.BOOL.fieldOf("overlay").forGetter(HiddenState::overlay),
                ).apply(it, ::HiddenState)
            },
            Codec.BOOL.xmap({ HiddenState(it, it) }, { it.overlay && it.base }),
        )
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
