package me.owdding.catharsis.features.area

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow

object AreaManager : SimplePreparableReloadListener<List<Pair<ResourceLocation, AreaDefinition>>>() {

    private val logger = Catharsis.featureLogger("Areas")
    private val converter = FileToIdConverter.json("catharsis/areas")
    private val gson = GsonBuilder().create()
    private val codec = CatharsisCodecs.getCodec<AreaDefinition>()

    private val areas: MutableMap<ResourceLocation, AreaDefinition> = mutableMapOf()

    override fun prepare(
        manager: ResourceManager,
        profiler: ProfilerFiller,
    ): List<Pair<ResourceLocation, AreaDefinition>> {
        return converter.listMatchingResources(manager)
            .mapNotNull { (id, resource) ->
                logger.runCatching("Error loading gui modifier $id") {
                    resource.openAsReader().use { reader ->
                        id to gson.fromJson(reader, JsonElement::class.java).toDataOrThrow(codec)
                    }
                }
            }
    }

    override fun apply(
        elements: List<Pair<ResourceLocation, AreaDefinition>>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ) {
        areas.clear()
        areas.putAll(elements.toMap())
    }

    @JvmStatic
    fun getLoadedAreas(): Map<ResourceLocation, AreaDefinition> = areas

    init {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(
            Catharsis.id("areas"),
            this,
        )
    }
}
