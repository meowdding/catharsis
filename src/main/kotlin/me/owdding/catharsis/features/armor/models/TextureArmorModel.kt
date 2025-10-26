package me.owdding.catharsis.features.armor.models

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

class TextureArmorModel(
    private val texture: ResourceLocation
) : ArmorModel {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ResourceLocation {
        return texture
    }

    class Unbaked(
        val texture: ResourceLocation,
    ) : ArmorModel.Unbaked {

        override val codec: MapCodec<out ArmorModel.Unbaked> = CODEC

        override fun bake(swapper: RegistryContextSwapper?): ArmorModel {
            return TextureArmorModel(texture)
        }

        companion object {

            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec { it.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
            ).apply(it, ::Unbaked) }
        }
    }
}
