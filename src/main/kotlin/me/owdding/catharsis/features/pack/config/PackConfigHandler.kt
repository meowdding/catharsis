package me.owdding.catharsis.features.pack.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.pack.meta.CatharsisMetadataSection
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.types.suggestion.IterableSuggestionProvider
import me.owdding.ktmodules.Module
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.minecraft.util.GsonHelper
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.extentions.associateByNotNull
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.time.currentInstant
import tech.thatgravyboat.skyblockapi.utils.time.since
import kotlin.io.path.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.time.isDistantPast

data class PackConfig(
    val packId: String,
    val default: JsonObject = JsonObject(),
    val current: JsonObject = JsonObject(),
) {

    fun set(id: String, value: JsonElement) {
        current.add(id, value)
    }

    fun get(id: String): JsonElement? {
        return current.get(id) ?: default.get(id)
    }

    fun options(): List<PackConfigOption>? = PackConfigHandler.catharsisPacks[packId]?.config?.takeUnless { it.isEmpty() }
}

@Module
object PackConfigHandler : ResourceManagerReloadListener {

    private const val SAVE_PATH = "catharsis/pack_configs.json"

    private val logger = CatharsisLogger.named("PackConfigHandler")
    private val path = McClient.config.resolve(SAVE_PATH)
    private val configs = mutableMapOf<String, PackConfig>()
    private var saveRequestedAt = Instant.DISTANT_PAST

    var catharsisPacks: Map<String, CatharsisMetadataSection> = emptyMap()
        private set

    init {
        logger.runCatching("Loading pack configurations") {
            if (path.notExists()) {
                path.parent?.createDirectories()
                path.createFile()
                logger.info("No existing config found")
                return@runCatching
            }
            val json = GsonHelper.parse(path.readText())
            for ((key, value) in json.entrySet()) {
                configs[key] = PackConfig(key, JsonObject(), value.asJsonObject)
            }
        }

        McClient.registerClientReloadListener(Catharsis.id("packconfig_handler"), this)
    }

    fun getConfig(packId: String): PackConfig {
        return configs.getOrPut(packId) { PackConfig(packId) }
    }

    fun save() {
        this.saveRequestedAt = currentInstant()
    }

    @Subscription(TickEvent::class)
    @TimePassed("10s")
    fun onTick() {
        if (!saveRequestedAt.isDistantPast && saveRequestedAt.since() >= 10.seconds) {
            val output = JsonObject()
            for ((key, value) in configs) {
                output.add(key, value.current.deepCopy())
            }

            Scheduling.async {
                logger.debug("Saving pack configurations to $SAVE_PATH")
                path.writeText(output.toString())
                saveRequestedAt = Instant.DISTANT_PAST
            }
        }
    }

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("catharsis config") {
            thenCallback("id", StringArgumentType.string(), IterableSuggestionProvider(catharsisPacks.keys)) {
                val id = argument<String>("id")
                val options = getConfig(id).options() ?: run {
                    Text.of("No config found for $id").sendWithPrefix()
                    return@thenCallback
                }
                McClient.setScreenAsync { PackConfigScreen(McScreen.self, id, options) }
            }
        }
    }

    override fun onResourceManagerReload(resourceManager: ResourceManager) {
        catharsisPacks = resourceManager.listPacks().toList().mapNotNull { pack ->
            pack.getMetadataSection(CatharsisMetadataSection.TYPE)?.let { it.id to it }
        }.toMap()
    }
}
