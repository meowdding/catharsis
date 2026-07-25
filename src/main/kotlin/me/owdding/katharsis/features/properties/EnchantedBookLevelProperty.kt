package me.owdding.katharsis.features.properties

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.toFloatValue

object EnchantedBookLevelProperty : RangeSelectItemModelProperty {

    val ID = Katharsis.id("enchanted_book_level")
    val CODEC: MapCodec<EnchantedBookLevelProperty> = MapCodec.unit { EnchantedBookLevelProperty }

    override fun get(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): Float {
        return stack.getData(DataTypes.ENCHANTMENTS).takeIf { it?.size == 1 }?.values?.firstOrNull()?.toFloat() ?: stack.getSkyBlockId()?.cleanId?.split(":")?.getOrNull(1).toFloatValue()
    }

    override fun type(): MapCodec<out RangeSelectItemModelProperty> = CODEC
}
