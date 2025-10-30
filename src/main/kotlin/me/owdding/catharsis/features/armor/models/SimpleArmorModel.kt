//~ item_holder
package me.owdding.catharsis.features.armor.models

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.catharsis.utils.geometry.BedrockGeometry
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

class SimpleArmorModel(private val state: ArmorModelState) : ArmorModel {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ArmorModelState {
        return state
    }

    @GenerateCodec
    data class UnbakedBedrock(
        val texture: ResourceLocation,
        val model: ResourceLocation,
    ) : ArmorModel.Unbaked {

        override val codec: MapCodec<out ArmorModel.Unbaked> = CatharsisCodecs.getMapCodec<UnbakedBedrock>()

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel {
            val geometry = resources.getOrLoad(this.model, BedrockGeometry.RESOURCE_PARSER)?.getOrThrow() ?: error("Could not find referenced bedrock geometry $model")
            return SimpleArmorModel(ArmorModelState.Bedrock(geometry.bake(), texture))
        }
    }

    @GenerateCodec
    data class UnbakedTexture(
        val texture: ResourceLocation,
    ) : ArmorModel.Unbaked {

        override val codec: MapCodec<out ArmorModel.Unbaked> = CatharsisCodecs.getMapCodec<UnbakedTexture>()

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel {
            return SimpleArmorModel(ArmorModelState.Texture(texture))
        }
    }
}


