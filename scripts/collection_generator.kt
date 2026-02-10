import com.google.gson.JsonObject
import com.google.gson.JsonParser
import it.unimi.dsi.fastutil.ints.IntArraySet
import me.owdding.catharsis.features.gui.definitions.GuiDefinition
import me.owdding.catharsis.features.gui.definitions.conditions.GuiDefinitionTitleCondition
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.catharsis.features.gui.definitions.slots.SlotAllCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotIndexCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotItemCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotNameCondition
import me.owdding.catharsis.features.gui.matchers.EqualsTextMatcher
import me.owdding.catharsis.utils.types.IntPredicate
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

fun fetch(): JsonObject {
    val client = HttpClient.newBuilder().build()
    val collections = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create("https://api.hypixel.net/v2/resources/skyblock/collections"))
            .build(),
        HttpResponse.BodyHandlers.ofString(),
    )

    return JsonParser.parseString(collections.body()).asJsonObject
}


fun createEntries(level: Int, index: Int, key: String, direction: String, name: String): List<GuiSlotDefinition> {
    return listOf(
        "unlocked" to Items.LIME_STAINED_GLASS_PANE,
        "unlocking" to Items.YELLOW_STAINED_GLASS_PANE,
        "locked" to Items.RED_STAINED_GLASS_PANE,
    ).map { (status, item) ->
        GuiSlotDefinition(
            id = Identifiers.of("skyblock_gui", "collections/$key/$status/$direction"),
            target = SlotAllCondition(
                conditions = listOf(
                    SlotIndexCondition(IntPredicate.Set(IntArraySet(listOf(index)))),
                    SlotItemCondition(setOf(item)),
                    SlotNameCondition(
                        matcher = EqualsTextMatcher(
                            setOf(
                                "$name $level",
                                "$name ${level.toRomanNumeral()}"
                            ),
                        ),
                    ),
                ),
            ),
        )
    }
}

fun collections() {
    val collections: JsonObject = fetch().getAsJsonObject("collections")

    collections.entrySet().forEach { (key, value) ->
        val key = key.lowercase()
        val items = value.asJsonObject.getAsJsonObject("items")
        items.entrySet().forEach { (_, value) ->
            val value = value.asJsonObject
            val name = value.get("name").asString

            val slotAmount = value.get("maxTiers").asInt

            fun createEntries(level: Int, index: Int, direction: String): List<GuiSlotDefinition> = createEntries(level, index, key, direction, name)

            val slots = when {
                slotAmount >= 9 -> {
                    (1..slotAmount).flatMap { level ->
                        val direction = when (level) {
                            slotAmount if level == 10 -> "start_end"
                            1, 10 -> "start"
                            9, slotAmount -> "end"
                            else -> "connector"
                        }

                        createEntries(level, 17 + level, direction)
                    }
                }

                slotAmount == 4 -> {
                    (1..slotAmount).flatMap { level ->
                        createEntries(level, 17 + level * 2, "start_end")
                    }
                }

                slotAmount % 2 == 0 -> {
                    val padding = (9 - (slotAmount + 1)) / 2
                    (1..slotAmount).flatMap { level ->
                        val direction = when (level) {
                            1 -> "start"
                            slotAmount -> "end"
                            slotAmount / 2 -> "connector_skip"
                            slotAmount / 2 + 1 -> "skip_connector"
                            else -> "connector"
                        }

                        createEntries(level, 17 + padding + level + (1.takeUnless { level <= slotAmount / 2 } ?: 0), direction)
                    }
                }

                else -> {
                    val padding = (9 - slotAmount + 1) / 2
                    (1..slotAmount).flatMap { level ->
                        val direction = when (level) {
                            1 -> "start"
                            slotAmount -> "end"
                            else -> "connector"
                        }

                        createEntries(level, 17 + padding + level, direction)
                    }
                }
            }

            val definition = GuiDefinition(
                target = GuiDefinitionTitleCondition(
                    title = Regex("$name Collection"),
                ),
                layout = slots
            )

            val path = Path("repo/guis/collections/$key/${name.lowercase().replace(" ", "_")}.json")
            path.createParentDirectories()
            path.writeText(definition.toJson(GuiDefinition.CODEC).prettyPrint())
        }
    }

}
