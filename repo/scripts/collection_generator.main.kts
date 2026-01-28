@file:DependsOn("com.google.code.gson:gson:2.13.2")

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

private val thousandsPlace = listOf("", "M", "MM", "MMM")
private val hundredsPlace = listOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
private val tensPlace = listOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
private val onesPlace = listOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")

fun Int.toRomanNumeral(): String {
    return thousandsPlace[this / 1000] + hundredsPlace[this % 1000 / 100] + tensPlace[this % 100 / 10] + onesPlace[this % 10]
}

fun fetch(): JsonObject {
    val client = HttpClient.newBuilder().build()
    val collections = client.send(
        HttpRequest.newBuilder()
            .uri(URI.create("https://api.hypixel.net/v2/resources/skyblock/collections"))
            .build(), HttpResponse.BodyHandlers.ofString()
    )

    return JsonParser.parseString(collections.body()).asJsonObject
}

val collections: JsonObject = fetch().getAsJsonObject("collections")

collections.entrySet().forEach { (key, value) ->
    val key = key.lowercase()
    val items = value.asJsonObject.getAsJsonObject("items")
    items.entrySet().forEach { (_, value) ->
        val value = value.asJsonObject
        val name = value.get("name").asString

        val slotAmount = value.get("maxTiers").asInt

        fun createEntries(level: Int, index: Int, direction: String): String = createEntries(level, index, key, direction, name)

        val slots = when {
            slotAmount >= 9 -> {
                (1..slotAmount).joinToString(",\n") { level ->
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
                (1..slotAmount).joinToString(",\n") { level ->
                    createEntries(level, 17 + level * 2, "start_end")
                }
            }
            slotAmount % 2 == 0 -> {
                val padding = (9 - (slotAmount + 1)) / 2
                (1..slotAmount).joinToString(",\n") { level ->
                    val direction = when (level) {
                        1 -> "start"
                        slotAmount -> "end"
                        slotAmount / 2  -> "connector_skip"
                        slotAmount / 2 + 1 -> "skip_connector"
                        else -> "connector"
                    }

                    createEntries(level, 17 + padding + level + (1.takeUnless { level <= slotAmount / 2 } ?: 0), direction)
                }
            }
            else -> {
                val padding = (9 - slotAmount + 1) / 2
                (1..slotAmount).joinToString(",\n") { level ->
                    val direction = when (level) {
                        1 -> "start"
                        slotAmount -> "end"
                        else -> "connector"
                    }

                    createEntries(level, 17 + padding + level, direction)
                }
            }
        }

        val definition = """
{
  "target": {
    "type": "title",
    "title": "$name Collection"
  },
  "layout": [
${slots.lines().joinToString("\n") { "    $it" }}
  ]
}
        """.trimIndent()

        val path = Path("../guis/collections/$key/${name.lowercase().replace(" ", "_")}.json")
        path.createParentDirectories()
        path.writeText(definition)
    }
}


fun createEntries(level: Int, index: Int, key: String, direction: String, name: String): String {
    return """
{
  "id": "skyblock_gui:collections/${key}/unlocked/${direction}",
  "target": {
    "type": "all",
    "conditions": [
      {
        "type": "slot",
        "slot": $index
      },
      {
        "type": "item",
        "items": "lime_stained_glass_pane"
      },
      {
        "type": "name",
        "mode": "equals",
        "name": [
          "$name $level",
          "$name ${level.toRomanNumeral()}"
        ]
      }
    ]
  }
},
{
  "id": "skyblock_gui:collections/${key}/unlocking/${direction}",
  "target": {
    "type": "all",
    "conditions": [
      {
        "type": "slot",
        "slot": $index
      },
      {
        "type": "item",
        "items": "yellow_stained_glass_pane"
      },
      {
        "type": "name",
        "mode": "equals",
        "name": [
          "$name $level",
          "$name ${level.toRomanNumeral()}"
        ]
      }
    ]
  }
},
{
  "id": "skyblock_gui:collections/${key}/locked/${direction}",
  "target": {
    "type": "all",
    "conditions": [
      {
        "type": "slot",
        "slot": $index
      },
      {
        "type": "item",
        "items": "red_stained_glass_pane"
      },
      {
        "type": "name",
        "mode": "equals",
        "name": [
          "$name $level",
          "$name ${level.toRomanNumeral()}"
        ]
      }
    ]
  }
}
                """.trimIndent()
}
