package me.owdding.catharsis.features.gui.definitions

import com.mojang.serialization.JsonOps
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.events.FinishRepoLoadEvent
import me.owdding.catharsis.events.GuiDefinitionsApplied
import me.owdding.catharsis.events.SlotChangedEvent
import me.owdding.catharsis.events.StartRepoLoadEvent
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.catharsis.features.imc.ImcHandler.isDisabled
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
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerCloseEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenInitializedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.io.path.readText


@Module
object GuiDefinitions : SimplePreparableReloadListener<Map<Identifier, GuiDefinition>>(), CatharsisLogger by Catharsis.featureLogger() {

    val uiDefinitionConverter: FileToIdConverter = FileToIdConverter.json("catharsis/guis")

    private val packDefinitions = mutableListOf<DefinitionEntry>()
    private val repoDefinitions = mutableListOf<DefinitionEntry>()
    private val definitions = mutableListOf<DefinitionEntry>()

    private var selected = listOf<DefinitionEntry>()
    private var slots = Int2ObjectArrayMap<GuiSlotDefinition>()

    private val updating = AtomicBoolean(false)

    internal fun enqueueUpdate() {
        if (updating.compareAndSet(false, true)) {
            McClient.runNextTick {
                McScreen.asMenu?.let(this::update)
                updating.set(false)
            }
        }
    }

    private fun update(screen: AbstractContainerScreen<*>?) {
        selected = screen?.let { definitions.filter { it.matches(screen) } } ?: emptyList()
        slots.clear()

        GuiDefinitionsApplied(selected.map { it.id }).post(SkyBlockAPI.eventBus)

        if (selected.isEmpty()) return
        if (screen == null) return

        for (slot in screen.menu.slots) {
            if (slot.item.isDisabled()) continue
            val definition = this.selected.findSlotDefinition(slot.index, slot.item)
            if (definition != null) {
                slots[slot.index] = definition
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
                uiDefinitionConverter.fileToId(key) to value.readWithCodec(GuiDefinition.CODEC)
            }
        }
    }

    override fun apply(data: Map<Identifier, GuiDefinition>, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        this.packDefinitions.clear()
        data.entries.forEach { (id, definition) ->
            this.packDefinitions.add(DefinitionEntry(id, definition))
        }
        sortDefinitions()
    }

    @Subscription
    private fun StartRepoLoadEvent.start() {
        repoDefinitions.clear()
        sortDefinitions()
    }

    @Subscription
    private fun FinishRepoLoadEvent.finish() {
        CatharsisRemoteRepo.listFilesInDirectory("guis").forEach { (name, path) ->
            val parsed = GuiDefinition.STRICT_CODEC.parse(JsonOps.INSTANCE, path.readText().readJson())
            val definition = parsed.resultOrPartial()

            if (McClient.isDev && parsed.isError) {
                GuiDefinitions.error("Failed to load gui definition from repo: $name", parsed.error().get().message())
            } else if (definition.isPresent) {
                repoDefinitions.add(DefinitionEntry(Catharsis.id(name.removeSuffix(".json")), definition.get()))
            }
        }
        sortDefinitions()
    }

    @Subscription
    fun onScreenOpen(event: ScreenInitializedEvent) = enqueueUpdate()

    @Subscription
    fun onInitialized(event: ContainerInitializedEvent) = enqueueUpdate()

    @Subscription
    fun onSlotChange(event: SlotChangedEvent) = enqueueUpdate()

    @Subscription
    fun onClose(event: ContainerCloseEvent) = enqueueUpdate()

    @JvmStatic
    fun getGuis(): List<Identifier> = selected.map { it.id }

    @JvmStatic
    fun getSlot(stack: ItemStack): Identifier? = this.selected.findSlotDefinition(-1, stack)?.id

    @JvmStatic
    fun getSlot(slot: Int): Identifier? = this.slots[slot]?.id

    private fun sortDefinitions() = McClient.runOrNextTick {
        this.definitions.clear()
        this.definitions.addAll(this.packDefinitions)
        this.definitions.addAll(this.repoDefinitions)
        this.definitions.sortByDescending { it.priority }
    }

    private fun Iterable<DefinitionEntry>.findSlotDefinition(slot: Int, stack: ItemStack): GuiSlotDefinition? {
        return this.firstNotNullOfOrNull {
            it.layout.find { def -> def.matches(slot, stack) }
        }
    }

    init {
        Catharsis.registerClientReloadListener(Catharsis.id("gui_definitions"), this)
    }

    private data class DefinitionEntry(val id: Identifier, val definition: GuiDefinition) {

        val priority: Int = definition.priority
        val layout: List<GuiSlotDefinition> = definition.layout

        fun matches(screen: AbstractContainerScreen<*>): Boolean {
            return definition.matches(screen)
        }
    }
}
