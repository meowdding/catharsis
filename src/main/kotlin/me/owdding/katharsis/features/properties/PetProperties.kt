package me.owdding.katharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.hooks.items.AbstractContainerScreenHook
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.utils.extentions.get


object PetItemProperty : SelectItemModelProperty<String> {

    val ID = Katharsis.id("pet_held_item")
    val TYPE: SelectItemModelProperty.Type<out SelectItemModelProperty<String>, String> = SelectItemModelProperty.Type.create(
        MapCodec.unit { PetItemProperty },
        KatharsisCodecs.getCodec(),
    )

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): String? = stack[DataTypes.PET_DATA]?.heldItem
    override fun valueCodec(): Codec<String> = Codec.STRING
    override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<String>, String> = TYPE
}

object PetSkinProperty : SelectItemModelProperty<String> {

    val ID = Katharsis.id("pet_skin")
    val TYPE: SelectItemModelProperty.Type<out SelectItemModelProperty<String>, String> = SelectItemModelProperty.Type.create(
        MapCodec.unit { PetSkinProperty },
        KatharsisCodecs.getCodec(),
    )

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): String? = stack[DataTypes.PET_DATA]?.skin
    override fun valueCodec(): Codec<String> = Codec.STRING
    override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<String>, String> = TYPE
}

object HasPetSkinProperty : ConditionalItemModelProperty {

    val ID: Identifier = Katharsis.id("has_pet_skin")
    val CODEC: MapCodec<HasPetSkinProperty> = MapCodec.unit(HasPetSkinProperty)

    override fun type(): MapCodec<HasPetSkinProperty> = CODEC

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, context: ItemDisplayContext): Boolean {
        return stack[DataTypes.PET_DATA]?.skin != null
    }
}


object PetCandyUsedProperty : RangeSelectItemModelProperty {

    val ID = Katharsis.id("pet_candy_used")
    val CODEC: MapCodec<PetCandyUsedProperty> = MapCodec.unit { PetCandyUsedProperty }

    override fun get(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): Float = stack[DataTypes.PET_DATA]?.candyUsed?.toFloat() ?: 0f
    override fun type(): MapCodec<out RangeSelectItemModelProperty> = CODEC
}

object PetExpProperty : RangeSelectItemModelProperty {

    val ID = Katharsis.id("pet_exp")
    val CODEC: MapCodec<PetExpProperty> = MapCodec.unit { PetExpProperty }

    override fun get(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): Float = stack[DataTypes.PET_DATA]?.exp?.toFloat() ?: 0f
    override fun type(): MapCodec<out RangeSelectItemModelProperty> = CODEC
}
