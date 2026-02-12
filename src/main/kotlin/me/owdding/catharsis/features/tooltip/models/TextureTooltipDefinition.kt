package me.owdding.catharsis.features.tooltip.models

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.features.tooltip.TooltipDefinition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.Identifier
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

class TextureTooltipDefinition(private val state: Identifier) : TooltipDefinition {

    override fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?): Identifier = state

    @GenerateCodec
    @NamedCodec("unbakedTextureTooltip")
    data class UnbakedTexture(
        val texture: Identifier,
    ) : TooltipDefinition.Unbaked {

        override val codec: MapCodec<out TooltipDefinition.Unbaked> = CatharsisCodecs.getMapCodec<UnbakedTexture>()

        override fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): TooltipDefinition {
            return TextureTooltipDefinition(this.texture)
        }
    }
}


