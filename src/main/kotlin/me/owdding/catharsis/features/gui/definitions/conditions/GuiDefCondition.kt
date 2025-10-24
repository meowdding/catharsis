package me.owdding.catharsis.features.gui.definitions.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs

interface GuiDefCondition {

    val codec: MapCodec<out GuiDefCondition>

    fun matches(screen: AbstractContainerScreen<*>): Boolean
}

object GuiDefConditions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out GuiDefCondition>>()

    @IncludedCodec
    val CODEC: Codec<GuiDefCondition> = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(GuiDefCondition::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("any"), CatharsisCodecs.getMapCodec<GuiDefAnyCondition>())
        ID_MAPPER.put(Catharsis.id("all"), CatharsisCodecs.getMapCodec<GuiDefAllCondition>())
        ID_MAPPER.put(Catharsis.id("slot"), CatharsisCodecs.getMapCodec<GuiDefSlotCondition>())
        ID_MAPPER.put(Catharsis.id("title"), CatharsisCodecs.getMapCodec<GuiDefTitleCondition>())
    }
}
