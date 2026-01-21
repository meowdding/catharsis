package me.owdding.catharsis.features.properties

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.utils.extentions.toFloatValue

object RuneLevelProperty : RangeSelectItemModelProperty {

    val ID = Catharsis.id("rune_level")
    val CODEC: MapCodec<RuneLevelProperty> = MapCodec.unit { RuneLevelProperty }

    override fun get(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): Float {
        return stack.getData(DataTypes.USED_RUNE)?.cleanId?.split(":")?.getOrNull(1).toFloatValue()
    }

    override fun type(): MapCodec<out RangeSelectItemModelProperty> = CODEC
}
