package me.owdding.catharsis.features.tooltip

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.ktmodules.Module
import net.minecraft.client.multiplayer.ClientRegistryLayer
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.PlaceholderLookupProvider
import net.minecraft.util.profiling.ProfilerFiller
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json.gson
import kotlin.jvm.optionals.getOrNull

@Module
object TooltipFeature : SimplePreparableReloadListener<TooltipDefinition?>() {

    private var definition: TooltipDefinition? = null
    private val modelMap = mutableMapOf<Identifier, TooltipDefinitionState>()

    override fun prepare(manager: ResourceManager, profiler: ProfilerFiller): TooltipDefinition? {
        val registry = ClientRegistryLayer.createRegistryAccess().compositeAccess()
        val resources = TypedResourceManager(manager)
        val lookup = PlaceholderLookupProvider(registry)
        val ops = lookup.createSerializationContext(JsonOps.INSTANCE)
        val swapper = lookup.createSwapper()

        val reader = manager.getResource(Catharsis.id("tooltip.json")).getOrNull()?.openAsReader() ?: return null
        return TooltipDefinitions.CODEC.parse(ops, gson.fromJson(reader, JsonElement::class.java)).orThrow.bake(swapper, resources)
    }

    override fun apply(definition: TooltipDefinition?, manager: ResourceManager, profiler: ProfilerFiller) {
        this.definition = definition
        modelMap.clear()
        definition?.let {
            modelMap.putAll(it.collectAll().associateBy { model -> model.identifier })
        }
    }

    @JvmStatic
    fun getDefinition(): TooltipDefinition? = definition

    @JvmStatic
    fun getBackground(id: Identifier?): Identifier? = modelMap[id]?.background

    @JvmStatic
    fun getFrame(id: Identifier?): Identifier? = modelMap[id]?.frame

    init {
        McClient.registerClientReloadListener(Catharsis.id("tooltip_definition"), this)
    }
}

