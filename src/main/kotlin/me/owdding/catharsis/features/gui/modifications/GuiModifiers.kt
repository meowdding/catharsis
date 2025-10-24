package me.owdding.catharsis.features.gui.modifications

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.gui.definitions.GuiDefinitions
import me.owdding.catharsis.features.gui.modifications.conditions.GuiModDefinitionCondition
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow

@Module
object GuiModifiers : SimplePreparableReloadListener<List<GuiModifier>>() {

    private val logger = CatharsisLogger.named("GuiModifiers")
    private val converter = FileToIdConverter.json("catharsis/gui_modifiers")
    private val gson = GsonBuilder().create()
    private val codec = CatharsisCodecs.getCodec<GuiModifier>()

    private val definitionModifiers: MutableMap<ResourceLocation, GuiModifier> = mutableMapOf()

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
                is GuiModDefinitionCondition -> {
                    val definition = modifier.target.definition
                    if (definitionModifiers.putIfAbsent(definition, modifier) != null) {
                        logger.error("Multiple gui modifiers found for definition $definition only one will be applied")
                    }
                }
            }
        }
    }

    @JvmStatic
    fun getActiveModifier(): GuiModifier? {
        return GuiDefinitions.getGui()?.let(definitionModifiers::get)
    }

    init {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(
            Catharsis.id("gui_modifiers"),
            this,
        )
    }
}
