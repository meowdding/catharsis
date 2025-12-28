package me.owdding.catharsis.features.gui.definitions

import com.google.gson.JsonElement
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.events.FinishRepoLoadEvent
import me.owdding.catharsis.events.StartRepoLoadEvent
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.repo.CatharsisRemoteRepo
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.CatharsisLogger.Companion.featureLogger
import me.owdding.catharsis.utils.extensions.mapBothNotNull
import me.owdding.catharsis.utils.extensions.readWithCodec
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerCloseEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenInitializedEvent
import tech.thatgravyboat.skyblockapi.utils.json.Json.gson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.io.Reader
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.io.path.reader


@Module
object GuiDefinitions : SimplePreparableReloadListener<Map<Identifier, GuiDefinition>>(), CatharsisLogger by Catharsis.featureLogger() {

    private val uiDefinitionConverter = FileToIdConverter.json("catharsis/guis")
    private val definitions = mutableMapOf<Identifier, GuiDefinition>()
    private val repoDefinitions = mutableMapOf<Identifier, GuiDefinition>()
    private val codec = CatharsisCodecs.GuiDefinitionCodec.codec()

    private var currentGui: Pair<Identifier, GuiDefinition>? = null
    private var slots: Int2ObjectMap<GuiSlotDefinition> = Int2ObjectArrayMap()

    private fun update(screen: AbstractContainerScreen<*>?) {
        currentGui = if (screen == null) {
            null
        } else if (currentGui?.second?.matches(screen) == true) {
            currentGui
        } else {
            definitions.entries.find { it.value.matches(screen) }?.toPair()
        }
        slots.clear()
        val layout = currentGui?.second?.layout ?: return
        val menuSlots = screen?.menu?.slots ?: return

        for (definition in layout) {
            for (slot in menuSlots) {
                if (definition.matches(slot.index, slot.item)) {
                    slots[slot.index] = definition
                }
            }
        }
    }

    override fun prepare(
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ): Map<Identifier, GuiDefinition> {
        return uiDefinitionConverter.listMatchingResources(resourceManager).mapBothNotNull { (key, value) ->
            if (key.namespace == Catharsis.MOD_ID) {
                GuiDefinitions.error("Tried to load gui definition in restricted namespace: $key")
                return@mapBothNotNull null
            }

            runCatching("Loading gui definition $key") {
                uiDefinitionConverter.fileToId(key) to value.readWithCodec(codec)
            }
        }
    }

    private fun Reader.parse() = gson.fromJson(this, JsonElement::class.java).toDataOrThrow(codec)

    override fun apply(
        data: Map<Identifier, GuiDefinition>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ) {
        this.definitions.clear()
        this.definitions.putAll(data)
        this.definitions.putAll(repoDefinitions)
    }

    @Subscription
    private fun StartRepoLoadEvent.start() {
        repoDefinitions.keys.forEach(definitions::remove)
        repoDefinitions.clear()
    }

    @Subscription
    private fun FinishRepoLoadEvent.finish() {
        CatharsisRemoteRepo.listFilesInDirectory("guis").forEach { (name, path) ->
            repoDefinitions[Catharsis.id(name.removeSuffix(".json"))] = path.reader().parse()
        }
        definitions.putAll(repoDefinitions)
    }

    @Subscription
    fun onScreenOpen(event: ScreenInitializedEvent) = update(event.screen as? AbstractContainerScreen<*>)

    @Subscription
    fun onInitialized(event: ContainerInitializedEvent) = update(event.screen)

    @Subscription
    fun onSlotChange(event: InventoryChangeEvent) = update(event.screen)

    @Subscription
    fun onClose(event: ContainerCloseEvent) = update(null)

    @JvmStatic
    fun getGui(): Identifier? = currentGui?.first

    @JvmStatic
    fun getSlot(slot: Int, stack: ItemStack): Identifier? = if (slot == -1) {
        currentGui?.second?.layout?.find { it.matches(-1, stack) }?.id
    } else {
        slots[slot]?.id
    }
}
