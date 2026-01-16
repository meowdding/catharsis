package me.owdding.catharsis.features.tooltip

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.tooltip.models.ConditionalTooltipModel
import me.owdding.catharsis.features.tooltip.models.RangeSelectTooltipModel
import me.owdding.catharsis.features.tooltip.models.SelectTooltipModel
import me.owdding.catharsis.features.tooltip.models.TextureTooltipModel
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack
import java.util.UUID

interface TooltipModel {

    fun resolve(stack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): TooltipModelState?
    fun collectAll(): List<TooltipModelState>

    interface Unbaked {

        val codec: MapCodec<out Unbaked>

        fun bake(swapper: RegistryContextSwapper?, resources: TypedResourceManager): TooltipModel
    }
}


data class TooltipModelState(
    val background: Identifier,
    val frame: Identifier,
) {
    val identifier = Catharsis.id(UUID.randomUUID().toString())
}

object TooltipModels {
    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out TooltipModel.Unbaked>>()

    @IncludedCodec
    val CODEC: Codec<TooltipModel.Unbaked> = ID_MAPPER.codec(Identifier.CODEC).dispatch(TooltipModel.Unbaked::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.mc("condition"), ConditionalTooltipModel.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.mc("range_dispatch"), RangeSelectTooltipModel.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.mc("select"), SelectTooltipModel.Unbaked.CODEC)
        ID_MAPPER.put(Catharsis.id("texture"), CatharsisCodecs.getMapCodec<TextureTooltipModel.UnbakedTexture>())
    }
}
