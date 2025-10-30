package me.owdding.catharsis.features.models

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.extensions.mapBothNotNull
import me.owdding.catharsis.utils.geometry.BedrockGeometry
import me.owdding.catharsis.utils.geometry.model.BedrockModelGeometryBaker
import me.owdding.catharsis.utils.geometry.model.MediumBakedBedrockModelGeometry
import me.owdding.ktmodules.Module
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow

@Module
object BedrockModels : SimplePreparableReloadListener<Map<ResourceLocation, BedrockGeometry>>() {
    private val geoModelConverter = FileToIdConverter("catharsis/geo_models", ".geo.json")
    private val logger = Catharsis.featureLogger("BlockReplacements")
    private val gson = GsonBuilder().create()

    private val models: MutableMap<ResourceLocation, MediumBakedBedrockModelGeometry> = mutableMapOf()

    override fun prepare(
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ): Map<ResourceLocation, BedrockGeometry> {
        return geoModelConverter.listMatchingResources(resourceManager).mapBothNotNull { (id, resource) ->
            geoModelConverter.fileToId(id) to logger.runCatching("Error loading block replacement definition $id") {
                resource.openAsReader().use { reader ->
                    gson.fromJson(reader, JsonElement::class.java).toDataOrThrow(BedrockGeometry.CODEC).first()
                }
            }
        }
    }

    override fun apply(
        loadedModels: Map<ResourceLocation, BedrockGeometry>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ) {
        models.clear()
        models.putAll(loadedModels.mapValues { BedrockModelGeometryBaker.bake(it.value) })
    }

    fun getModel(location: ResourceLocation) = models[location]

    init {
       // Utils.registerClientReloadListener(Catharsis.id("bedrock_models"), this, ResourceReloaderKeys.BEFORE_VANILLA)
    }
}
