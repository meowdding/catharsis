package me.owdding.katharsis.features.pack

import com.google.gson.reflect.TypeToken
import me.owdding.katharsis.Katharsis
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.json.Json
import java.util.function.Predicate
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Module
object PackOrderRetainer {
    var isSaving: Boolean = false
    private var savedOrder: List<String> = emptyList()

    private val savePath = McClient.config.resolve("katharsis/pack_order.json")

    private const val SERVER_PACK = "SERVER_PACK_PLACEHOLDER"

    init {
        loadOrder()
    }

    private fun loadOrder() {
        try {
            if (savePath.exists()) {
                savedOrder = Json.gson.fromJson(savePath.readText(), object : TypeToken<List<String>>() {}.type) ?: emptyList()
            }
        } catch (e: Exception) {
            Katharsis.error("Pack order could not be loaded: ${savePath.readText()}", e)
        }
    }

    fun saveCurrentOrder(packIds: Collection<String>, isServerPack: Predicate<String>) {
        val newOrder = mutableListOf<String>()
        var foundServerPack = false

        for (id in packIds) {
            if (isServerPack.test(id)) {
                newOrder.add(SERVER_PACK)
                foundServerPack = true
            } else {
                newOrder.add(id)
            }
        }

        if (!foundServerPack && savedOrder.contains(SERVER_PACK)) {
            val oldIndex = savedOrder.indexOf(SERVER_PACK)
            newOrder.add(oldIndex.coerceAtMost(newOrder.size), SERVER_PACK)
        }

        savedOrder = newOrder

        Scheduling.async {
            try {
                savePath.parent?.createDirectories()
                savePath.writeText(Json.gson.toJson(savedOrder))
            } catch (e: Exception) {
                Katharsis.error("Pack order could not be saved: ${e.message}", e)
            }
        }
    }

    fun restoreOrder(packIds: Collection<String>, isServerPack: Predicate<String>): Collection<String> {
        if (savedOrder.isEmpty()) return packIds

        return packIds.sortedBy { id ->

            val searchId = if (isServerPack.test(id)) SERVER_PACK else id
            val index = savedOrder.indexOf(searchId)
            if (index != -1) index else Int.MAX_VALUE
        }
    }
}
