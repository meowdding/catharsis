package me.owdding.catharsis.features.entity

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktmodules.Module
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow

@Module
object CustomEntityDefinitions : SimplePreparableReloadListener<Map<Identifier, CustomEntityDefinition>>() {

    private val logger = Catharsis.featureLogger("EntityDefinitions")
    private val converter = FileToIdConverter.json("catharsis/entity_definitions")
    private val gson = GsonBuilder().create()
    private val codec = CatharsisCodecs.getCodec<CustomEntityDefinition>()

    private val definitions = mutableMapOf<EntityType<*>, MutableMap<Identifier, CustomEntityDefinition>>()

    override fun prepare(
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ): Map<Identifier, CustomEntityDefinition> {
        return converter.listMatchingResources(resourceManager)
            .mapNotNull { (file, resource) ->
                logger.runCatching("Error loading entity definition $file") {
                    resource.openAsReader().use { bufferedReader ->
                        val id = converter.fileToId(file)
                        val definition = gson.fromJson(bufferedReader, JsonElement::class.java).toDataOrThrow(codec)

                        id to definition
                    }
                }
            }
            .associate { it }
    }

    override fun apply(
        definitions: Map<Identifier, CustomEntityDefinition>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ) {
        this.definitions.clear()
        for ((id, definition) in definitions.entries) {
            this.definitions.computeIfAbsent(definition.type) { mutableMapOf() }[id] = definition
        }
    }

    @JvmStatic
    fun getFor(entity: Entity): Identifier? {
        val type = entity.type ?: return null
        return definitions[type]?.entries?.firstOrNull { it.value.matches(entity) }?.key
    }

    init {
        McClient.registerClientReloadListener(Catharsis.id("entity_definitions"), this)
    }
}
