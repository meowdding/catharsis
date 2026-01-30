package me.owdding.catharsis.features.gui.modifications

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.events.GuiDefinitionsApplied
import me.owdding.catharsis.features.gui.modifications.conditions.GuiModifierDefinitionCondition
import me.owdding.catharsis.features.gui.modifications.modifiers.SlotModifier
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktmodules.Module
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import org.joml.Vector2i
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow

@Module
object GuiModifiers : SimplePreparableReloadListener<List<GuiModifier>>() {

    private val logger = Catharsis.featureLogger("GuiModifiers")
    private val converter = FileToIdConverter.json("catharsis/gui_modifiers")
    private val gson = GsonBuilder().create()
    private val codec = CatharsisCodecs.getCodec<GuiModifier>()

    private val definitionModifiers: MutableMap<Identifier, MutableList<GuiModifier>> = mutableMapOf()

    private var selected: GuiModifier? = null

    override fun prepare(manager: ResourceManager, profiler: ProfilerFiller): List<GuiModifier> {
        return converter.listMatchingResources(manager)
            .mapNotNull { (id, resource) ->
                logger.runCatching("Error loading gui modifier $id") {
                    resource.openAsReader().use { reader ->
                        gson.fromJson(reader, JsonElement::class.java).toDataOrThrow(codec)
                    }
                }
            }
    }

    override fun apply(modifiers: List<GuiModifier>, manager: ResourceManager, profiler: ProfilerFiller) {
        definitionModifiers.clear()
        for (modifier in modifiers) {
            when (modifier.target) {
                is GuiModifierDefinitionCondition -> {
                    val definition = modifier.target.definition
                    val modifiers = definitionModifiers.getOrPut(definition) { mutableListOf() }
                    modifiers.add(modifier)
                }
            }
        }
    }

    @JvmStatic
    fun getActiveModifier(): GuiModifier? {
        return this.selected
    }

    @Subscription
    fun onGuiDefinitionsApplied(event: GuiDefinitionsApplied) {
        val modifiers = event.definitions
            .mapNotNull(definitionModifiers::get)
            .flatten()

        if (modifiers.isEmpty()) {
            this.selected = null
        } else {
            val slots = mutableMapOf<Identifier, SlotModifier>()
            for (modifier in modifiers) {
                modifier.slots.forEach { (identifier, modifier) ->
                    slots.putIfAbsent(identifier, modifier)
                }
            }

            this.selected = GuiModifier(
                target = modifiers.first().target, // Target is not relevant for combined modifier
                overrideLabels = modifiers.any { it.overrideLabels },
                overrideBackground =  modifiers.any { it.overrideBackground },
                bounds = modifiers.mapNotNull { it.bounds }.let { sizes ->
                    if (sizes.isEmpty()) null else { Vector2i(sizes.maxOf { it.x }, sizes.maxOf { it.y }) }
                },
                slots = slots,
                elements = modifiers.flatMap { it.elements },
                widgets = modifiers.flatMap { it.widgets }
            )
        }
    }

    init {
        McClient.registerClientReloadListener(Catharsis.id("gui_modifiers"), this)
    }
}
