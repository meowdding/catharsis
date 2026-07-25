package me.owdding.katharsis.features.armor.models

import com.mojang.serialization.MapCodec
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.utils.TypedResourceManager
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.client.color.item.ItemTintSource
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.Identifier
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

class TextureArmorModel(
    private val textures: Array<Identifier>,
    private val tints: List<ItemTintSource>,
    private val isTranslucent: Boolean,
) : ArmorModel {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): ArmorModelState {
        val tints = IntArray(textures.size) {
            val source = this.tints.getOrNull(it) ?: return@IntArray -1
            source.calculate(stack, level, owner?.asLivingEntity())
        }
        return ArmorModelState.Texture(this.textures, tints, this.isTranslucent)
    }

    @GenerateCodec
    data class UnbakedTexture(
        val layers: List<Identifier>,
        val tints: List<ItemTintSource> = listOf(),
        val translucent: Boolean = false
    ) : ArmorModel.Unbaked {

        override val codec: MapCodec<out ArmorModel.Unbaked> = KatharsisCodecs.getMapCodec<UnbakedTexture>()

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): ArmorModel {
            return TextureArmorModel(this.layers.toTypedArray(), this.tints, this.translucent)
        }
    }
}


