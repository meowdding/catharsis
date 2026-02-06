import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

private val thousandsPlace = listOf("", "M", "MM", "MMM")
private val hundredsPlace = listOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
private val tensPlace = listOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
private val onesPlace = listOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")

fun Int.toRomanNumeral(): String {
    return thousandsPlace[this / 1000] + hundredsPlace[this % 1000 / 100] + tensPlace[this % 100 / 10] + onesPlace[this % 10]
}

fun createSkill(skillName: String, maxLevel: Int, nodeItem: String?, pathName: String = skillName, type: String = "Level", title: String = "$skillName Skill", icon: String = title, extra: String = "", dataType: String = "skill", pluraleDataType: String = "${dataType}s") {

    val nodes = (1..maxLevel).joinToString(",\n") { level ->
        val evalLevel = level % 25

        var direction = when {
            level % 25 >= 23 -> "up_down"
            level % 25 == 0 -> "up_end"
            level % 25 == 1 -> "start_down"

            evalLevel % 10 == 0 -> "left_down"
            evalLevel % 10 <= 2 -> "up_down"
            evalLevel % 10 == 3 -> "up_right"
            evalLevel % 10 == 4 || evalLevel % 10 == 9 -> "left_right"
            evalLevel % 10 == 5 -> "left_up"
            evalLevel % 10 <= 7 -> "down_up"
            else -> "down_right"
        }

        if (maxLevel == level) {
            direction = direction.substringBefore("_") + "_end"
        }

        """
{
  "id": "skyblock_gui:${pluraleDataType}/${extra}${skillName.lowercase()}/unlocked/${direction}",
  "target": {
    "type": "all",
    "conditions": [
      {
        "type": "item",
        "items": [
          "lime_stained_glass_pane"${nodeItem?.takeIf { level % 5 == 0 }?.let { ",\n          \"$it\"" } ?: ""}
        ]
      },
      {
        "type": "name",
        "mode": "equals",
        "name": [
          "$skillName $type ${level.toRomanNumeral()}",
          "$skillName $type $level"
        ]
      }
    ]
  }
},
{
  "id": "skyblock_gui:${pluraleDataType}/${extra}${skillName.lowercase()}/unlocking/${direction}",
  "target": {
    "type": "all",
    "conditions": [
      {
        "type": "item",
        "items": "yellow_stained_glass_pane"
      },
      {
        "type": "name",
        "mode": "equals",
        "name": [
          "$skillName $type ${level.toRomanNumeral()}",
          "$skillName $type $level"
        ]
      }
    ]
  }
},
{
  "id": "skyblock_gui:${pluraleDataType}/${extra}${skillName.lowercase()}/locked/${direction}",
  "target": {
    "type": "all",
    "conditions": [
      {
        "type": "item",
        "items": "red_stained_glass_pane"
      },
      {
        "type": "name",
        "mode": "equals",
        "name": [
          "$skillName $type ${level.toRomanNumeral()}",
          "$skillName $type $level"
        ]
      }
    ]
  }
}
        """.trimIndent().lines().joinToString(separator = "\n") { "    $it" }
    }


    val base = """
{
  "target": {
    "type": "title",
    "title": "$title"
  },
  "layout": [
    {
      "id": "skyblock_gui:${pluraleDataType}/${extra}${skillName.lowercase()}/icon",
      "target": {
        "type": "all",
        "conditions": [
          {
            "type": "name",
            "name": "$icon"
          },
          {
            "type": "slot",
            "slot": 0
          }
        ]
      }
    },
$nodes
  ]
}
    """.trimIndent()

    Path.of("../guis/${pluraleDataType}/${pathName.lowercase()}.json").apply {
        createParentDirectories()
    }.writeText(base)
}

createSkill("Combat", 60, "diamond_helmet")
createSkill("Farming", 60, "hay_block")
createSkill("Fishing", 50, "prismarine")
createSkill("Mining", 60, "iron_block")
createSkill("Foraging", 54, "oak_log")
createSkill("Enchanting", 60, "enchanted_book")
createSkill("Alchemy", 50, "blaze_rod")
createSkill("Carpentry", 50, "armor_stand")
createSkill("Taming", 60, "golden_carrot")
createSkill("Hunting", 60, null)
createSkill("Catacombs", 50, "player_head", type = "Mastery", title = "Catacombs Rewards", icon = "Catacombs", pathName = "dungeoneering/catacombs/catacombs", extra = "dungeoneering/")

createSkill("Runecrafting", 25, "end_portal_frame")
createSkill("Social", 25, "blaze_powder")

createSkill("Healer", 50, "splash_potion", title = "Healer Class Perks", icon = "Healer Class", dataType = "class", pluraleDataType = "classes")
createSkill("Mage", 50, "blaze_rod", title = "Mage Class Perks", icon = "Mage Class", dataType = "class", pluraleDataType = "classes")
createSkill("Berserk", 50, "iron_sword", title = "Berserk Class Perks", icon = "Berserk Class", dataType = "class", pluraleDataType = "classes")
createSkill("Archer", 50, "bow", title = "Archer Class Perks", icon = "Archer Class", dataType = "class", pluraleDataType = "classes")
createSkill("Tank", 50, "leather_chestplate", title = "Tank Class Perks", icon = "Tank Class", dataType = "class", pluraleDataType = "classes")
