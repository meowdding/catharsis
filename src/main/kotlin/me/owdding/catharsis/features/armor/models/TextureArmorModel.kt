//~ item_holder
package me.owdding.catharsis.features.armor.models

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.color.item.ItemTintSource
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

class TextureArmorModel(
    private val textures: Array<ResourceLocation>,
    private val tints: List<ItemTintSource>,
) : ArmorModel {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ArmorModelState {
        val tints = IntArray(textures.size) {
            val source = this.tints.getOrNull(it) ?: return@IntArray -1
            source.calculate(stack, level, owner?.asLivingEntity())
        }
        return ArmorModelState.Texture(this.textures, tints)
    }

    @GenerateCodec
    data class UnbakedTexture(
        val layers: List<ResourceLocation>,
        val tints: List<ItemTintSource> = listOf(),
    ) : ArmorModel.Unbaked {

        override val codec: MapCodec<out ArmorModel.Unbaked> = CatharsisCodecs.getMapCodec<UnbakedTexture>()

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel {
            return TextureArmorModel(this.layers.toTypedArray(), this.tints)
        }
    }
}


