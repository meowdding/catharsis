package me.owdding.katharsis.features.pack.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.features.pack.meta.KatharsisMetadataSection
import me.owdding.katharsis.utils.KatharsisLogger
import me.owdding.katharsis.utils.extensions.sendWithPrefix
import me.owdding.katharsis.utils.types.suggestion.IterableSuggestionProvider
import me.owdding.ktmodules.Module
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
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.text.Text
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

    fun options(): List<PackConfigOption>? = PackConfigHandler.katharsisPackOptions[packId]?.takeUnless(List<PackConfigOption>::isEmpty)
}

@Module
object PackConfigHandler : ResourceManagerReloadListener {

    private const val SAVE_PATH = "katharsis/pack_configs.json"

    private val logger = KatharsisLogger.named("PackConfigHandler")
    private val path = McClient.config.resolve(SAVE_PATH)
    private val configs = mutableMapOf<String, PackConfig>()
    private var saveRequestedAt = Instant.DISTANT_PAST

    var katharsisPackOptions: Map<String, List<PackConfigOption>?> = emptyMap()
        private set

    init {
        logger.runCatching("Loading pack configurations") {
            if (path.notExists()) {
                path.parent?.createDirectories()
                path.createFile()
                logger.info("No existing config found")
                return@runCatching
            }
            val json = GsonHelper.parse(path.readText().ifBlank { "{}" })
            for ((key, value) in json.entrySet()) {
                configs[key] = PackConfig(key, JsonObject(), value.asJsonObject)
            }
        }

        Katharsis.registerClientReloadListener(Katharsis.id("packconfig_handler"), this)
    }

    fun getConfig(packId: String): PackConfig {
        return configs.getOrPut(packId) { PackConfig(packId) }
    }

    fun save() {
        this.saveRequestedAt = currentInstant()
    }

    @JvmStatic
    fun updateDefaults(id: String, options: List<PackConfigOption>) {
        val config = getConfig(id).default
        config.asMap().clear()
        for (option in options) {
            option.addToDefault(config)
        }
    }

    @JvmStatic
    fun isLoaded(id: String): Boolean {
        return katharsisPackOptions.containsKey(id)
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
        event.register("katharsis config") {
            then("id", StringArgumentType.string(), IterableSuggestionProvider(katharsisPackOptions.keys)) {
                callback {
                    val id = argument<String>("id")
                    openPackConfigScreen(id)
                }
                then("search", StringArgumentType.string()) {
                    callback {
                        val id = argument<String>("id")
                        val search = argument<String>("search")
                        openPackConfigScreen(id, search)
                    }
                }
            }
        }
    }

    fun openPackConfigScreen(id: String, search: String = "") {
        val options = getConfig(id).options() ?: run {
            Text.of("No config found for $id").sendWithPrefix()
            return
        }
        McClient.setScreenAsync { PackConfigScreen(McScreen.self, id, options, search) }
    }

    override fun onResourceManagerReload(resourceManager: ResourceManager) {
        katharsisPackOptions = resourceManager.listPacks().toList().mapNotNull { pack ->
            val meta = pack.getMetadataSection(KatharsisMetadataSection.TYPE) ?: return@mapNotNull null
            val options = PackConfigOption.fromResource(pack)
            val config = options?.takeUnless(List<PackConfigOption>::isEmpty) ?: meta.config
            updateDefaults(meta.id, config)
            meta.id to config
        }.toMap()
    }
}
