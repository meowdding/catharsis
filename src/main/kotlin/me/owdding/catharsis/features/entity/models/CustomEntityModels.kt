package me.owdding.catharsis.features.entity.models

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.ktmodules.Module
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityAttributesUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityEquipmentUpdateEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow

@Module
object CustomEntityModels : SimplePreparableReloadListener<Map<Identifier, CustomEntityModel>>() {

    private val logger = Catharsis.featureLogger("EntityModels")
    private val converter = FileToIdConverter.json("catharsis/entities")
    private val gson = GsonBuilder().create()
    private val codec = CatharsisCodecs.getCodec<CustomEntityModel.Unbaked>()

    private val definitions = mutableMapOf<Identifier, CustomEntityModel>()

    override fun prepare(
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ): Map<Identifier, CustomEntityModel> {
        val resources = TypedResourceManager(resourceManager)

        return converter.listMatchingResources(resourceManager)
            .mapNotNull { (fileName, resource) ->
                logger.runCatching("Error loading entity model $fileName") {
                    resource.openAsReader().use { bufferedReader ->
                        val definition = gson.fromJson(bufferedReader, JsonElement::class.java).toDataOrThrow(codec).bake(resources)

                        val id = converter.fileToId(fileName)

                        id to definition
                    }
                }
            }
            .associate { it }
    }

    override fun apply(
        definitions: Map<Identifier, CustomEntityModel>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ) {
        this.definitions.clear()
        this.definitions.putAll(definitions)

        if (McLevel.hasLevel) {
            for (entity in McLevel.level.entitiesForRendering()) {
                entity.`catharsis$resetCustomModel`()
            }
        }
    }

    @Subscription
    private fun EntityAttributesUpdateEvent.onAttributeUpdate() {
        this.entity.`catharsis$resetCustomModel`()
    }

    @Subscription
    private fun EntityEquipmentUpdateEvent.onEquipmentUpdate() {
        this.entity.`catharsis$resetCustomModel`()
    }

    @JvmStatic
    fun getModel(identifier: Identifier): CustomEntityModel? {
        return definitions[identifier]
    }

    init {
        McClient.registerClientReloadListener(Catharsis.id("entity_replacements"), this)
    }
}
