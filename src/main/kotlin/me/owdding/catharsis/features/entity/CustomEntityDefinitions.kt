package me.owdding.catharsis.features.entity

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.events.FinishRepoLoadEvent
import me.owdding.catharsis.events.StartRepoLoadEvent
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.catharsis.repo.CatharsisRemoteRepo
import me.owdding.ktmodules.Module
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.io.Reader
import kotlin.io.path.reader

@Module
object CustomEntityDefinitions : SimplePreparableReloadListener<Map<Identifier, CustomEntityDefinition>>() {

    private val logger = Catharsis.featureLogger("EntityDefinitions")
    private val converter = FileToIdConverter.json("catharsis/entity_definitions")
    private val gson = GsonBuilder().create()
    private val codec = CatharsisCodecs.getCodec<CustomEntityDefinition>()

    private val definitions = mutableMapOf<EntityType<*>, MutableMap<Identifier, CustomEntityDefinition>>()
    private val repoDefinitions = mutableMapOf<EntityType<*>, MutableMap<Identifier, CustomEntityDefinition>>()

    private fun Reader.parse() = gson.fromJson(this, JsonElement::class.java).toDataOrThrow(codec)

    override fun prepare(
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ): Map<Identifier, CustomEntityDefinition> {
        return converter.listMatchingResources(resourceManager)
            .mapNotNull { (file, resource) ->
                logger.runCatching("Error loading entity definition $file") {
                    resource.openAsReader().use { reader ->
                        val id = converter.fileToId(file)
                        id to reader.parse()
                    }
                }
            }
            .associate { it }
    }

    @Subscription
    private fun StartRepoLoadEvent.start() {
        repoDefinitions.forEach { (type, map) ->
            map.keys.forEach { id ->
                definitions[type]?.remove(id)
            }
        }
        repoDefinitions.clear()
    }

    @Subscription
    private fun FinishRepoLoadEvent.finish() {
        CatharsisRemoteRepo.listFilesInDirectory("entities").forEach { (name, path) ->
            logger.runCatching("Error loading remote entity definition $name") {
                val id = Catharsis.id(name.removeSuffix(".json"))
                val definition = path.reader().use { it.parse() }
                repoDefinitions.computeIfAbsent(definition.type) { mutableMapOf() }[id] = definition
            }
        }

        repoDefinitions.forEach { (type, map) ->
            definitions.computeIfAbsent(type) { mutableMapOf() }.putAll(map)
        }
    }

    override fun apply(
        elements: Map<Identifier, CustomEntityDefinition>,
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ) {
        this.definitions.clear()

        repoDefinitions.forEach { (type, map) ->
            this.definitions.computeIfAbsent(type) { mutableMapOf() }.putAll(map)
        }

        for ((id, definition) in elements.entries) {
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
