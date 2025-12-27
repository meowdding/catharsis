package me.owdding.catharsis.features.gui.definitions.conditions

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.codecs.IncludedCodecs
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs

interface GuiDefinitionCondition {

    val codec: MapCodec<out GuiDefinitionCondition>

    fun matches(screen: AbstractContainerScreen<*>): Boolean
}

object GuiDefConditions {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out GuiDefinitionCondition>>()

    @IncludedCodec
    val CODEC: Codec<GuiDefinitionCondition> = ID_MAPPER.codec(IncludedCodecs.catharsisIdentifier).dispatch(GuiDefinitionCondition::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("any"), CatharsisCodecs.getMapCodec<GuiDefinitionAnyCondition>())
        ID_MAPPER.put(Catharsis.id("all"), CatharsisCodecs.getMapCodec<GuiDefinitionAllCondition>())
        ID_MAPPER.put(Catharsis.id("slot"), CatharsisCodecs.getMapCodec<GuiDefinitionSlotCondition>())
        ID_MAPPER.put(Catharsis.id("title"), CatharsisCodecs.getMapCodec<GuiDefinitionTitleCondition>())
    }
}
