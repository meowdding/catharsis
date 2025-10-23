package me.owdding.catharsis.features.gui.definitions

import com.google.gson.JsonObject
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.gui.definitions.conditions.GuiCondition
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.Slot
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerCloseEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenInitializedEvent
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import kotlin.io.path.extension
import kotlin.io.path.readText
import kotlin.io.path.walk

@GenerateCodec
data class GuiDefinition(
    val id: ResourceLocation,
    val condition: GuiCondition,
    val layout: List<GuiSlotDefinition>,
) {

    fun matches(screen: AbstractContainerScreen<*>): Boolean {
        return condition.matches(screen)
    }
}

@Module
object GuiDefinitions {

    private val definitions = mutableMapOf<ResourceLocation, GuiDefinition>()

    init {
        // TODO needs to be loaded remotely or something idk
        val localDefinitions = FabricLoader.getInstance().configDir.resolve("catharsis/guis").walk()
        for (paths in localDefinitions) {
            if (paths.extension != "json") continue
            runCatching {
                val definition = paths.readText().readJson<JsonObject>().toDataOrThrow(CatharsisCodecs.getCodec<GuiDefinition>())
                definitions[definition.id] = definition
            }.onFailure {
                Catharsis.error("Failed to load GUI definition from ${paths.fileName}", it)
            }
        }
    }

    private var currentGui: GuiDefinition? = null
    private var slots: Int2ObjectMap<GuiSlotDefinition> = Int2ObjectArrayMap()

    private fun update(screen: AbstractContainerScreen<*>?) {
        currentGui = if (screen == null) {
            null
        } else if (currentGui?.matches(screen) == true) {
            currentGui
        } else {
            definitions.values.find { it.matches(screen) }
        }
        slots.clear()
        val layout = currentGui?.layout ?: return
        val menuSlots = screen?.menu?.slots ?: return

        for (definition in layout) {
            for (slot in menuSlots) {
                if (definition.matches(slot)) {
                    slots[slot.index] = definition
                }
            }
        }
    }

    @Subscription fun onScreenOpen(event: ScreenInitializedEvent) = update(event.screen as? AbstractContainerScreen<*>)
    @Subscription fun onInitialized(event: ContainerInitializedEvent) = update(event.screen)
    @Subscription fun onSlotChange(event: InventoryChangeEvent) = update(event.screen)
    @Subscription fun onClose(event: ContainerCloseEvent) = update(null)

    @JvmStatic fun getGui(): ResourceLocation? = currentGui?.id
    @JvmStatic fun getSlot(slot: Slot?): ResourceLocation? = slot?.let { slots[slot.index]?.id }
}
