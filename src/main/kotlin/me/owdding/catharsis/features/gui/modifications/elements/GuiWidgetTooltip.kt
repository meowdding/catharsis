package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.gui.modifications.elements.interactions.GuiSlotClickWidgetInteraction
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McScreen

interface GuiWidgetTooltip {
    val codec: MapCodec<out GuiWidgetTooltip>

    fun getTooltip(element: GuiWidgetElement): List<Component>?
}

@GenerateCodec
data class TextWidgetTooltip(@Compact val text: List<Component>) : GuiWidgetTooltip {
    override val codec = CatharsisCodecs.getMapCodec<TextWidgetTooltip>()

    override fun getTooltip(element: GuiWidgetElement): List<Component> = text
}

@GenerateCodec
data class SlotWidgetTooltip(val slot: Int) : GuiWidgetTooltip {
    override val codec = CatharsisCodecs.getMapCodec<SlotWidgetTooltip>()

    override fun getTooltip(element: GuiWidgetElement): List<Component>? {
        return getSlotTooltip(slot)
    }
}

object InteractionWidgetTooltip : GuiWidgetTooltip {
    override val codec = MapCodec.unit(this)

    override fun getTooltip(element: GuiWidgetElement): List<Component>? {
        val interaction = element.interaction
        if (interaction is GuiSlotClickWidgetInteraction) {
            return getSlotTooltip(interaction.slot)
        }
        return null
    }
}

private fun getSlotTooltip(slotIndex: Int): List<Component>? {
    val menu = McScreen.asMenu?.menu ?: return null
    val itemStack = menu.getSlot(slotIndex).takeIf { it != null && it.index == slotIndex }?.item ?: return null
    if (itemStack.isEmpty) return null
    val player = McPlayer.self ?: return null

    return itemStack.getTooltipLines(Item.TooltipContext.of(player.level()), player, TooltipFlag.Default.NORMAL)
}

object WidgetTooltips {

    val ID_MAPPER = ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<out GuiWidgetTooltip>>()

    @IncludedCodec
    val CODEC: Codec<GuiWidgetTooltip> = ID_MAPPER.codec(Identifier.CODEC).dispatch(GuiWidgetTooltip::codec) { it }

    init {
        ID_MAPPER.put(Catharsis.id("text"), CatharsisCodecs.getMapCodec<TextWidgetTooltip>())
        ID_MAPPER.put(Catharsis.id("slot"), CatharsisCodecs.getMapCodec<SlotWidgetTooltip>())
        ID_MAPPER.put(Catharsis.id("interaction"), InteractionWidgetTooltip.codec)
    }
}
