package me.owdding.catharsis.features.properties

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.environmental.DateTimeAPI

object SkyBlockSeasonProperty : SelectItemModelProperty<String> {

    val ID = Catharsis.id("skyblock_season")
    val TYPE: SelectItemModelProperty.Type<out SelectItemModelProperty<String>, String> = SelectItemModelProperty.Type.create(
        MapCodec.unit { SkyBlockSeasonProperty },
        Codec.STRING,
    )

    override fun get(stack: ItemStack, level: ClientLevel?, entity: LivingEntity?, seed: Int, displayContext: ItemDisplayContext): String? {
        return DateTimeAPI.season?.name
    }

    override fun valueCodec(): Codec<String> = Codec.STRING
    override fun type(): SelectItemModelProperty.Type<out SelectItemModelProperty<String>, String> = TYPE
}

object SkyBlockDayProperty : RangeSelectItemModelProperty {

    val ID = Catharsis.id("skyblock_day")
    val CODEC = MapCodec.unit { SkyBlockDayProperty }

    override fun get(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): Float {
        return DateTimeAPI.day.toFloat()
    }

    override fun type(): MapCodec<out RangeSelectItemModelProperty> = CODEC
}

object SkyBlockHourProperty : RangeSelectItemModelProperty {

    val ID = Catharsis.id("skyblock_hour")
    val CODEC = MapCodec.unit { SkyBlockHourProperty }

    override fun get(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): Float {
        return DateTimeAPI.hour.toFloat()
    }

    override fun type(): MapCodec<out RangeSelectItemModelProperty> = CODEC
}

