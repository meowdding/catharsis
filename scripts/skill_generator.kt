import me.owdding.catharsis.features.gui.definitions.GuiDefinition
import me.owdding.catharsis.features.gui.definitions.conditions.GuiDefinitionTitleCondition
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.catharsis.features.gui.definitions.slots.SlotAllCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotAnyCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotIndexCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotItemCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotNameCondition
import me.owdding.catharsis.features.gui.matchers.EqualsTextMatcher
import me.owdding.catharsis.features.gui.matchers.RegexTextMatcher
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText


fun createSkill(
    skillName: String,
    maxLevel: Int,
    nodeItem: Item?,
    pathName: String = skillName,
    type: String = "Level",
    title: String = "$skillName Skill",
    icon: String = title,
    extra: String = "",
    dataType: String = "skill",
    pluralDataType: String = "${dataType}s",
    extraSlots: () -> List<GuiSlotDefinition> = ::emptyList,
) {
    val nodes = (1..maxLevel).flatMap { level ->
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

        listOf(
            "unlocked" to Items.LIME_STAINED_GLASS_PANE,
            "unlocking" to Items.YELLOW_STAINED_GLASS_PANE,
            "locked" to Items.RED_STAINED_GLASS_PANE,
        ).map { (status, item) ->
            GuiSlotDefinition(
                id = Identifiers.of("skyblock_gui", "$pluralDataType/$extra${skillName.lowercase()}/$status/$direction"),
                target = SlotAllCondition(
                    conditions = listOf(
                        SlotItemCondition(
                            buildSet {
                                add(item)
                                if (level % 5 == 0 && status == "unlocked") nodeItem?.let(::add)
                            },
                        ),
                        SlotNameCondition(
                            matcher = EqualsTextMatcher(
                                setOf(
                                    "$skillName $type $level",
                                    "$skillName $type ${level.toRomanNumeral()}",
                                ),
                            ),
                        ),
                    ),
                ),
            )
        }
    }

    val definition = GuiDefinition(
        target = GuiDefinitionTitleCondition(Regex(title)),
        layout = buildList {
            add(
                GuiSlotDefinition(
                    id = Identifiers.of("skyblock_gui", "${pluralDataType}/${extra}${skillName.lowercase()}/icon"),
                    target = SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher(setOf(icon))),
                        SlotIndexCondition(0),
                    ),
                ),
            )
            addAll(nodes)
            addAll(extraSlots())
        },
    )

    Path("repo/guis/${pluralDataType}/${pathName.lowercase()}.json").apply {
        createParentDirectories()
    }.writeText(definition.toJson(GuiDefinition.CODEC).prettyPrint())
}

fun combat(): List<GuiSlotDefinition> = listOf(
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/combat/bestiary"),
        target = SlotAllCondition(
            SlotIndexCondition(39),
            SlotNameCondition(EqualsTextMatcher("Bestiary")),
        ),
    ),
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/combat/slayer"),
        target = SlotAllCondition(
            SlotIndexCondition(41),
            SlotNameCondition(EqualsTextMatcher("Slayer")),
        ),
    ),
)

fun farming(): List<GuiSlotDefinition> = listOf(
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/farming/garden"),
        target = SlotAllCondition(
            SlotIndexCondition(39),
            SlotItemCondition(setOf(Items.SUNFLOWER)),
            SlotAnyCondition(
                SlotNameCondition(RegexTextMatcher("Garden Level \\d+")),
                SlotNameCondition(RegexTextMatcher("Garden Level [XIV]+"))
            ),
        ),
    ),
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/farming/chips"),
        target = SlotAllCondition(
            SlotIndexCondition(41),
            SlotNameCondition(EqualsTextMatcher("Manage Chips")),
        ),
    ),
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/farming/farming_contests"),
        target = SlotAllCondition(
            SlotIndexCondition(45),
            SlotNameCondition(EqualsTextMatcher("Jacob's Farming Contests")),
        ),
    ),
)

fun fishing(): List<GuiSlotDefinition> = listOf(
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/fishing/bait_guide"),
        target = SlotAllCondition(
            SlotIndexCondition(39),
            SlotItemCondition(setOf(Items.BOOK)),
            SlotNameCondition(EqualsTextMatcher("Bait Guide")),
        ),
    ),
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/fishing/sea_creature_guide"),
        target = SlotAllCondition(
            SlotIndexCondition(40),
            SlotItemCondition(setOf(Items.BOOK)),
            SlotNameCondition(EqualsTextMatcher("Sea Creature Guide")),
        ),
    ),
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/fishing/rod_part_guide"),
        target = SlotAllCondition(
            SlotIndexCondition(41),
            SlotItemCondition(setOf(Items.BOOK)),
            SlotNameCondition(EqualsTextMatcher("Rod Part Guide")),
        ),
    ),
)

fun mining(): List<GuiSlotDefinition> = listOf(
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/mining/heart_of_the_mountain"),
        target = SlotAllCondition(
            SlotIndexCondition(39),
            SlotItemCondition(setOf(Items.PLAYER_HEAD)),
            SlotNameCondition(EqualsTextMatcher("Heart of the Mountain")),
        ),
    ),
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/mining/handy_block_guide"),
        target = SlotAllCondition(
            SlotIndexCondition(41),
            SlotItemCondition(setOf(Items.BOOK)),
            SlotNameCondition(EqualsTextMatcher("Handy Block Guide")),
        ),
    ),
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/mining/rock_pet_milestone"),
        target = SlotAllCondition(
            SlotIndexCondition(51),
            SlotItemCondition(setOf(Items.PLAYER_HEAD)),
            SlotNameCondition(EqualsTextMatcher("Rock Pet Milestone")),
        ),
    ),
)

fun foraging(): List<GuiSlotDefinition> = listOf(
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/foraging/heart_of_the_forest"),
        target = SlotAllCondition(
            SlotIndexCondition(40),
            SlotItemCondition(setOf(Items.PLAYER_HEAD)),
            SlotNameCondition(EqualsTextMatcher("Heart of the Forest")),
        ),
    ),
)


fun enchanting(): List<GuiSlotDefinition> = listOf(
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/enchanting/enchantments_guide"),
        target = SlotAllCondition(
            SlotIndexCondition(40),
            SlotItemCondition(setOf(Items.BOOK)),
            SlotNameCondition(EqualsTextMatcher("Enchantments Guide")),
        ),
    ),
)

fun runecrafting(): List<GuiSlotDefinition> = listOf(
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/runecrafting/xp_multiplier"),
        target = SlotAllCondition(
            SlotIndexCondition(45),
            SlotItemCondition(setOf(Items.DIAMOND)),
            SlotNameCondition(EqualsTextMatcher("Runecrafting: XP Multiplier")),
        ),
    ),
)

fun hunting(): List<GuiSlotDefinition> = listOf(
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/hunting/hunting_box"),
        target = SlotAllCondition(
            SlotIndexCondition(39),
            SlotItemCondition(setOf(Items.CHEST)),
            SlotNameCondition(EqualsTextMatcher("Hunting Box")),
        ),
    ),
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/hunting/attribute_menu"),
        target = SlotAllCondition(
            SlotIndexCondition(41),
            SlotItemCondition(setOf(Items.LEAD)),
            SlotNameCondition(EqualsTextMatcher("Attribute Menu")),
        ),
    ),
)

fun catacombs(): List<GuiSlotDefinition> = listOf(
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/dungeoneering/catacombs/catacombs_profile"),
        target = SlotAllCondition(
            SlotIndexCondition(45),
            SlotItemCondition(setOf(Items.OAK_SIGN)),
            SlotNameCondition(EqualsTextMatcher("Catacombs Profile")),
        ),
    ),
    GuiSlotDefinition(
        id = Identifiers.of("skyblock_gui", "skills/dungeoneering/catacombs/catacombs_rng_meter"),
        target = SlotAllCondition(
            SlotIndexCondition(40),
            SlotItemCondition(setOf(Items.PLAYER_HEAD)),
            SlotNameCondition(EqualsTextMatcher("Catacombs RNG Meters")),
        ),
    ),
)

fun skills() {
    createSkill("Combat", 60, Items.DIAMOND_HELMET, extraSlots = ::combat)
    createSkill("Farming", 60, Items.HAY_BLOCK, extraSlots = ::farming)
    createSkill("Fishing", 50, Items.PRISMARINE, extraSlots = ::fishing)
    createSkill("Mining", 60, Items.IRON_BLOCK, extraSlots = ::mining)
    createSkill("Foraging", 54, Items.OAK_LOG, extraSlots = ::foraging)
    createSkill("Enchanting", 60, Items.ENCHANTED_BOOK, extraSlots = ::enchanting)
    createSkill("Alchemy", 50, Items.BLAZE_ROD)
    createSkill("Carpentry", 50, Items.ARMOR_STAND)
    createSkill("Taming", 60, Items.GOLDEN_CARROT)
    createSkill("Hunting", 60, null, extraSlots = ::hunting)
    createSkill(
        "Catacombs",
        50,
        Items.PLAYER_HEAD,
        type = "Mastery",
        title = "Catacombs Rewards",
        icon = "Catacombs",
        pathName = "dungeoneering/catacombs/catacombs",
        extra = "dungeoneering/",
        extraSlots = ::catacombs,
    )

    createSkill("Runecrafting", 25, Items.END_PORTAL_FRAME, extraSlots = ::runecrafting)
    createSkill("Social", 25, Items.BLAZE_POWDER)

    createSkill("Healer", 50, Items.SPLASH_POTION, title = "Healer Class Perks", icon = "Healer Class", dataType = "class", pluralDataType = "classes")
    createSkill("Mage", 50, Items.BLAZE_ROD, title = "Mage Class Perks", icon = "Mage Class", dataType = "class", pluralDataType = "classes")
    createSkill("Berserk", 50, Items.IRON_SWORD, title = "Berserk Class Perks", icon = "Berserk Class", dataType = "class", pluralDataType = "classes")
    createSkill("Archer", 50, Items.BOW, title = "Archer Class Perks", icon = "Archer Class", dataType = "class", pluralDataType = "classes")
    createSkill("Tank", 50, Items.LEATHER_CHESTPLATE, title = "Tank Class Perks", icon = "Tank Class", dataType = "class", pluralDataType = "classes")
}
