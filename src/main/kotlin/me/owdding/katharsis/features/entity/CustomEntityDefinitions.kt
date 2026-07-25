package me.owdding.katharsis.features.entity

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.events.FinishRepoLoadEvent
import me.owdding.katharsis.events.StartRepoLoadEvent
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.katharsis.repo.KatharsisRemoteRepo
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
import java.io.Reader
import kotlin.io.path.reader

@Module
object CustomEntityDefinitions : SimplePreparableReloadListener<Map<Identifier, CustomEntityDefinition>>() {

    private val logger = Katharsis.featureLogger("EntityDefinitions")
    private val converter = FileToIdConverter.json("katharsis/entity_definitions")
    private val gson = GsonBuilder().create()
    private val codec = KatharsisCodecs.getCodec<CustomEntityDefinition>()

    private val definitions = mutableMapOf<EntityType<*>, MutableMap<Identifier, CustomEntityDefinition>>()
    private val repoDefinitions = mutableMapOf<EntityType<*>, MutableMap<Identifier, CustomEntityDefinition>>()

    private fun Reader.parse(name: String): CustomEntityDefinition? {
        val element = gson.fromJson(this, JsonElement::class.java)
        val parsed = codec.parse(com.mojang.serialization.JsonOps.INSTANCE, element)
        parsed.error().ifPresent {
            logger.error("Failed to load entity definition $name\nContext: ${it.message()}")
        }
        return parsed.resultOrPartial().orElse(null)
    }

    override fun prepare(
        resourceManager: ResourceManager,
        profiler: ProfilerFiller,
    ): Map<Identifier, CustomEntityDefinition> {
        return converter.listMatchingResources(resourceManager)
            .mapNotNull { (file, resource) ->
                logger.runCatching("Error loading entity definition $file") {
                    resource.openAsReader().use { reader ->
                        val id = converter.fileToId(file)
                        reader.parse(file.toString())?.let { id to it }
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
        KatharsisRemoteRepo.listFilesInDirectory("entities").forEach { (name, path) ->
            logger.runCatching("Error loading remote entity definition $name") {
                val id = Katharsis.id(name.removeSuffix(".json"))
                val definition = path.reader().use { it.parse(name) }
                if (definition != null) {
                    repoDefinitions.computeIfAbsent(definition.type) { mutableMapOf() }[id] = definition
                }
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

    @JvmStatic
    fun getDefinition(id: Identifier): CustomEntityDefinition? {
        return definitions.values.firstNotNullOfOrNull { it[id] }
    }

    @JvmStatic
    fun getAllIds(): Iterable<Identifier> {
        return definitions.values.flatMap { it.keys }
    }

    init {
        McClient.registerClientReloadListener(Katharsis.id("entity_definitions"), this)
    }
}
