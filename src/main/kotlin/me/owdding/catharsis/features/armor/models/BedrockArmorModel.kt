//~ item_holder
package me.owdding.catharsis.features.armor.models

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.catharsis.utils.geometry.BakedBedrockGeometry
import me.owdding.catharsis.utils.geometry.BedrockGeometry
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.color.item.ItemTintSource
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

class BedrockArmorModel(
    private val geoemtry: BakedBedrockGeometry,
    private val textures: Array<ResourceLocation>,
    private val tints: List<ItemTintSource>,
) : ArmorModel {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ArmorModelState {
        val tints = IntArray(textures.size) {
            val source = this.tints.getOrNull(it) ?: return@IntArray -1
            source.calculate(stack, level, owner?.asLivingEntity())
        }
        return ArmorModelState.Bedrock(this.geoemtry, this.textures, tints)
    }

    @GenerateCodec
    data class UnbakedBedrock(
        val model: ResourceLocation,
        val layers: List<ResourceLocation>,
        val tints: List<ItemTintSource> = listOf(),
    ) : ArmorModel.Unbaked {

        override val codec: MapCodec<out ArmorModel.Unbaked> = CatharsisCodecs.getMapCodec<UnbakedBedrock>()

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel {
            val geometry = resources.getOrLoad(this.model, BedrockGeometry.RESOURCE_PARSER)?.getOrThrow() ?: error("Could not find referenced bedrock geometry $model")
            return BedrockArmorModel(geometry.bake(), this.layers.toTypedArray(), this.tints)
        }
    }

}


