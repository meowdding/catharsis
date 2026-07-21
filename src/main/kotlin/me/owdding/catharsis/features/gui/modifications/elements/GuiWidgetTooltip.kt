package me.owdding.catharsis.features.gui.modifications.elements

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.gui.definitions.GuiDefinitions
import me.owdding.catharsis.features.gui.modifications.elements.interactions.GuiSlotClickWidgetInteraction
import me.owdding.catharsis.features.gui.modifications.elements.interactions.GuiSlotIdClickWidgetInteraction
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.Item
import net.minecraft.world.item.TooltipFlag
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines

interface GuiWidgetTooltip {
    val codec: MapCodec<out GuiWidgetTooltip>

    fun getTooltip(element: GuiWidgetElement): List<Component>?
}

@GenerateCodec
data class TextWidgetTooltip(@Compact val text: List<Component>) : GuiWidgetTooltip {
    private val splitText = text.flatMap { it.splitLines() }
    override val codec = CatharsisCodecs.getMapCodec<TextWidgetTooltip>()

    override fun getTooltip(element: GuiWidgetElement): List<Component> = splitText
}

@GenerateCodec
data class SlotWidgetTooltip(val slot: Int) : GuiWidgetTooltip {
    override val codec = CatharsisCodecs.getMapCodec<SlotWidgetTooltip>()

    override fun getTooltip(element: GuiWidgetElement): List<Component>? {
        return getSlotTooltip(slot)
    }
}

@GenerateCodec
data class SlotIdWidgetTooltip(val slot: Identifier) : GuiWidgetTooltip {
    override val codec = CatharsisCodecs.getMapCodec<SlotIdWidgetTooltip>()

    fun getSlot() = McScreen.asMenu?.menu?.slots?.firstOrNull { GuiDefinitions.getSlot(it.index) == slot }

    override fun getTooltip(element: GuiWidgetElement): List<Component>? {
        val slot = getSlot() ?: return null
        return getSlotTooltip(slot.index)
    }
}

@GenerateCodec
data class SkyBlockIdWidgetTooltip(val id: SkyBlockId, val withName: Boolean = true, val withLore: Boolean = true) : GuiWidgetTooltip {
    override val codec = CatharsisCodecs.getMapCodec<SkyBlockIdWidgetTooltip>()

    override fun getTooltip(element: GuiWidgetElement) = buildList {
        val item = id.toItem()
        if (withName) add(item.hoverName)
        if (withLore) addAll(item.getLore())
    }
}

object InteractionWidgetTooltip : GuiWidgetTooltip {
    override val codec = MapCodec.unit(this)

    override fun getTooltip(element: GuiWidgetElement): List<Component>? {
        val interaction = element.interaction
        if (interaction is GuiSlotClickWidgetInteraction) {
            return getSlotTooltip(interaction.slot)
        }
        if (interaction is GuiSlotIdClickWidgetInteraction) {
            interaction.getSlot()?.index?.let {
                return getSlotTooltip(it)
            }
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
        ID_MAPPER.put(Catharsis.id("slot_id"), CatharsisCodecs.getMapCodec<SlotWidgetTooltip>())
        ID_MAPPER.put(Catharsis.id("id"), CatharsisCodecs.getMapCodec<SkyBlockIdWidgetTooltip>())
        ID_MAPPER.put(Catharsis.id("interaction"), InteractionWidgetTooltip.codec)
    }
}
