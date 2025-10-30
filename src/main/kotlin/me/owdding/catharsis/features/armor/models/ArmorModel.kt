//~ item_holder
package me.owdding.catharsis.features.armor.models

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.catharsis.utils.geometry.BakedBedrockGeometry
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

interface ArmorModel {

    fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ArmorModelState

    interface Unbaked {

        val codec: MapCodec<out Unbaked>

        fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel
    }
}

sealed interface ArmorModelState {

    object Missing : Texture(MissingTextureAtlasSprite.getLocation())

    open class Texture(val texture: ResourceLocation): ArmorModelState
    open class Bedrock(val geometry: BakedBedrockGeometry, val texture: ResourceLocation) : ArmorModelState
}

object ArmorModels {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out ArmorModel.Unbaked>>()

    @IncludedCodec
    val CODEC: Codec<ArmorModel.Unbaked> = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(ArmorModel.Unbaked::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.mc("condition"), ConditionalArmorModel.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.mc("range_dispatch"), RangeSelectArmorModel.Unbaked.CODEC)
        // TODO add select, requires creating a whole custom case system as vanilla has this locked to ItemModels unlike conditions and ranges
        ID_MAPPER.put(Catharsis.id("texture"), CatharsisCodecs.getMapCodec<SimpleArmorModel.UnbakedTexture>())
        ID_MAPPER.put(Catharsis.id("model"), CatharsisCodecs.getMapCodec<SimpleArmorModel.UnbakedBedrock>())
    }
}
