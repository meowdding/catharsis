package me.owdding.katharsis.features.armor.models

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.TypedResourceManager
import me.owdding.katharsis.utils.geometry.BakedBedrockGeometry
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.resources.Identifier
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

    object Missing : Texture(arrayOf(MissingTextureAtlasSprite.getLocation()), intArrayOf(-1))
    object Fallthrough : ArmorModelState

    open class Texture(val textures: Array<Identifier>, val colors: IntArray, val isTranslucent: Boolean = false) : ArmorModelState {

        val layers: Int = this.textures.size

        init {
            check(colors.size == layers) { "Colors array size (${colors.size}) must match textures array size ($layers)" }
        }
    }

    open class Bedrock(val geometry: BakedBedrockGeometry, val textures: Array<Identifier>, val colors: IntArray, val isTranslucent: Boolean = false) : ArmorModelState {

        val layers: Int = this.textures.size

        init {
            check(colors.size == layers) { "Colors array size (${colors.size}) must match textures array size ($layers)" }
        }
    }
}

object ArmorModels {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out ArmorModel.Unbaked>>()

    @IncludedCodec
    val CODEC: Codec<ArmorModel.Unbaked> = ID_MAPPER.codec(Identifier.CODEC).dispatch(ArmorModel.Unbaked::codec) { it }

    init {
        ID_MAPPER.put(Katharsis.mc("condition"), ConditionalArmorModel.Unbaked.CODEC)
        ID_MAPPER.put(Katharsis.mc("range_dispatch"), RangeSelectArmorModel.Unbaked.CODEC)
        ID_MAPPER.put(Katharsis.mc("select"), SelectArmorModel.Unbaked.CODEC)
        ID_MAPPER.put(Katharsis.id("texture"), KatharsisCodecs.getMapCodec<TextureArmorModel.UnbakedTexture>())
        ID_MAPPER.put(Katharsis.id("model"), KatharsisCodecs.getMapCodec<BedrockArmorModel.UnbakedBedrock>())
        ID_MAPPER.put(Katharsis.id("redirect"), KatharsisCodecs.getMapCodec<RedirectedArmorModel.UnbakedRedirect>())
        ID_MAPPER.put(Katharsis.id("fallthrough"), FallThroughArmorModel.codec)
        ID_MAPPER.put(Katharsis.id("missing"), MissingArmorModel.codec)
    }
}
