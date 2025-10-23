package me.owdding.catharsis.features.gui.definitions.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ExtraCodecs

interface GuiCondition {

    val codec: MapCodec<out GuiCondition>

    fun matches(screen: AbstractContainerScreen<*>): Boolean
}

object GuiConditions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<out GuiCondition>>()

    @IncludedCodec
    val CODEC: Codec<GuiCondition> = ID_MAPPER.codec(ResourceLocation.CODEC).dispatch(GuiCondition::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("any"), CatharsisCodecs.getMapCodec<GuiAnyCondition>())
        ID_MAPPER.put(Catharsis.id("all"), CatharsisCodecs.getMapCodec<GuiAllCondition>())
        ID_MAPPER.put(Catharsis.id("slot"), CatharsisCodecs.getMapCodec<GuiSlotCondition>())
        ID_MAPPER.put(Catharsis.id("title"), CatharsisCodecs.getMapCodec<GuiTitleCondition>())
    }
}
