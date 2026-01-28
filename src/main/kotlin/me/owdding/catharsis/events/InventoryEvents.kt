package me.owdding.catharsis.events

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.resources.Identifier
import net.minecraft.world.inventory.Slot
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class SlotChangedEvent(
    val slot: Slot,
    val screen: AbstractContainerScreen<*>,
) : SkyBlockEvent()

class GuiDefinitionsApplied(
    val definitions: List<Identifier>,
) : SkyBlockEvent()
