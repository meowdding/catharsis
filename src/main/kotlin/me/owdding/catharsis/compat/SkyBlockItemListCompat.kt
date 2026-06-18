package me.owdding.catharsis.compat
//? >= 26.1 {

import com.operationpotato.itemlist.api.ExcludedScreensManager
import com.operationpotato.itemlist.api.ExclusionZoneManager
import com.operationpotato.itemlist.api.Plugin
import me.owdding.catharsis.features.gui.modifications.GuiModifiers
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.Rect2i
import java.util.*

object SkyBlockItemListCompat : Plugin {
    override fun registerExcludedScreens(excludedScreensManager: ExcludedScreensManager) {
        excludedScreensManager.addProvider(Screen::class.java) { _ ->
            val activeModifier = GuiModifiers.getActiveModifier()
            if (activeModifier != null && activeModifier.disableItemList) {
                Optional.of("Catharsis")
            } else {
                Optional.empty()
            }
        }
    }

    override fun registerExclusionZones(exclusionZoneManager: ExclusionZoneManager) {
        exclusionZoneManager.addProvider(Screen::class.java) { _ ->
            val activeModifier = GuiModifiers.getActiveModifier()
            if (activeModifier != null && activeModifier.itemListExclusionZones.isNotEmpty()) {
                activeModifier.itemListExclusionZones.map { zone ->
                    Rect2i(zone.x, zone.y, zone.width, zone.height)
                }
            } else {
                emptyList()
            }
        }
    }
}
//?}
