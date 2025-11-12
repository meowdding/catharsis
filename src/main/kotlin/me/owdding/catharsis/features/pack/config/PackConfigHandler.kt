package me.owdding.catharsis.features.pack.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.ktmodules.Module
import net.minecraft.util.GsonHelper
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import kotlin.io.path.readText
import kotlin.io.path.writeText

data class PackConfig(
    val default: JsonObject = JsonObject(),
    val current: JsonObject = JsonObject()
) {

    fun set(id: String, value: JsonElement) {
        current.add(id, value)
    }

    fun get(id: String): JsonElement? {
        return current.get(id) ?: default.get(id)
    }
}

@Module
object PackConfigHandler {

    private const val SAVE_PATH = "catharsis/pack_configs.json"

    private val logger = CatharsisLogger.named("PackConfigHandler")
    private val path = McClient.config.resolve(SAVE_PATH)
    private val configs = mutableMapOf<String, PackConfig>()
    private var saveRequestedAt = 0L

    init {
        logger.runCatching("Loading pack configurations") {
            val json = GsonHelper.parse(path.readText())
            for ((key, value) in json.entrySet()) {
                configs[key] = PackConfig(JsonObject(), value.asJsonObject)
            }
        }
    }

    fun getConfig(packId: String): PackConfig {
        return configs.getOrPut(packId) { PackConfig() }
    }

    fun save() {
        this.saveRequestedAt = System.currentTimeMillis()
    }

    @Subscription(TickEvent::class)
    @TimePassed("10s")
    fun onTick() {
        if (saveRequestedAt != 0L && System.currentTimeMillis() - saveRequestedAt >= 10000L) {
            val output = JsonObject()
            for ((key, value) in configs) {
                output.add(key, value.current.deepCopy())
            }

            Scheduling.async {
                logger.debug("Saving pack configurations to $SAVE_PATH")
                path.writeText(output.toString())
            }
        }
    }
}
