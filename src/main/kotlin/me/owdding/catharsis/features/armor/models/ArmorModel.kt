//~ item_holder
package me.owdding.catharsis.features.armor.models

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

interface ArmorModel {

    fun resolve(
        stack: ItemStack,
        level: ClientLevel?,
        owner: ItemOwner?,
        seed: Int,
    ): ResourceLocation

    interface Unbaked {

        val codec: MapCodec<out Unbaked>

        fun bake(swapper: RegistryContextSwapper?): ArmorModel
    }
}

object ArmorModels {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out ArmorModel.Unbaked>>()
    val MISSING_TEXTURE: ResourceLocation = ResourceLocation.withDefaultNamespace("missingno")

    @IncludedCodec
    val CODEC: Codec<ArmorModel.Unbaked> = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(ArmorModel.Unbaked::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.mc("condition"), ConditionalArmorModel.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.mc("range_dispatch"), RangeSelectArmorModel.Unbaked.CODEC)
        // TODO add select, requires creating a whole custom case system as vanilla has this locked to ItemModels unlike conditions and ranges
        ID_MAPPER.put(Catharsis.id("texture"), TextureArmorModel.Unbaked.CODEC)
    }
}
