package me.owdding.catharsis.features.tooltip

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.tooltip.models.ConditionalTooltipDefinition
import me.owdding.catharsis.features.tooltip.models.RangeSelectTooltipDefinition
import me.owdding.catharsis.features.tooltip.models.SelectTooltipDefinition
import me.owdding.catharsis.features.tooltip.models.TextureTooltipDefinition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

interface TooltipDefinition {

    fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?): Identifier?

    interface Unbaked {

        val codec: MapCodec<out Unbaked>

        fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): TooltipDefinition
    }
}

object TooltipDefinitions {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out TooltipDefinition.Unbaked>>()

    @IncludedCodec
    val CODEC: Codec<TooltipDefinition.Unbaked> = ID_MAPPER.codec(Identifier.CODEC).dispatch(TooltipDefinition.Unbaked::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.mc("condition"), ConditionalTooltipDefinition.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.mc("range_dispatch"), RangeSelectTooltipDefinition.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.mc("select"), SelectTooltipDefinition.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.id("texture"), CatharsisCodecs.getMapCodec<TextureTooltipDefinition.UnbakedTexture>())
    }
}
