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
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

interface ArmorModel {

    fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ArmorModelState?

    interface Unbaked {

        val codec: MapCodec<out Unbaked>

        fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel
    }
}

sealed interface ArmorModelState {

    object Missing : Texture(arrayOf(MissingTextureAtlasSprite.getLocation()), intArrayOf(-1))

    open class Texture(val textures: Array<Identifier>, val colors: IntArray) : ArmorModelState {

        val layers: Int = this.textures.size

        init {
            check(colors.size == layers) { "Colors array size (${colors.size}) must match textures array size ($layers)" }
        }
    }

    open class Bedrock(val geometry: BakedBedrockGeometry, val textures: Array<Identifier>, val colors: IntArray) : ArmorModelState {

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
        ID_MAPPER.put(Catharsis.mc("condition"), ConditionalArmorModel.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.mc("range_dispatch"), RangeSelectArmorModel.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.mc("select"), SelectArmorModel.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.id("texture"), CatharsisCodecs.getMapCodec<TextureArmorModel.UnbakedTexture>())
        ID_MAPPER.put(Catharsis.id("model"), CatharsisCodecs.getMapCodec<BedrockArmorModel.UnbakedBedrock>())
        ID_MAPPER.put(Catharsis.id("redirect"), CatharsisCodecs.getMapCodec<RedirectedArmorModel.UnbakedRedirect>())
        ID_MAPPER.put(Catharsis.id("fallthrough"), FallThroughArmorModel.codec)
        ID_MAPPER.put(Catharsis.id("missing"), MissingArmorModel.codec)
    }
}
