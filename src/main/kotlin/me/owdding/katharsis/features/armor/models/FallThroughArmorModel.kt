package me.owdding.katharsis.features.armor.models

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.utils.TypedResourceManager
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

object FallThroughArmorModel : ArmorModel, ArmorModel.Unbaked {
    override fun resolve(
        stack: ItemStack,
        level: ClientLevel?,
        owner: ItemOwner?,
        seed: Int,
    ): ArmorModelState = ArmorModelState.Fallthrough

    override val codec: MapCodec<out ArmorModel.Unbaked> = MapCodec.unit(this)
    override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel = this

}
