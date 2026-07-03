import com.mojang.datafixers.util.Either
import me.owdding.catharsis.features.gui.definitions.GuiDefinition
import me.owdding.catharsis.features.gui.definitions.conditions.GuiDefinitionTitleCondition
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.catharsis.features.gui.definitions.slots.SlotAllCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotItemCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotLoreCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotNameCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotNotCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotIndexCondition
import me.owdding.catharsis.features.gui.matchers.EqualsTextMatcher
import me.owdding.catharsis.features.gui.matchers.RegexTextMatcher
import me.owdding.catharsis.features.gui.matchers.TextMatcher
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.platform.Identifiers.of
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

val mainMenuSlotIndexes = mapOf(
    "dynamic/spawn" to SlotIndexCondition(10),
    "hub/spawn" to SlotIndexCondition(11),
    "dungeon_hub/spawn" to SlotIndexCondition(12),
    "farming_1/spawn" to SlotIndexCondition(13),
    "foraging_1/spawn" to SlotIndexCondition(14),
    "foraging_2/spawn" to SlotIndexCondition(15),
    "mining_1/spawn" to SlotIndexCondition(16),
    "mining_2/spawn" to SlotIndexCondition(19),
    "mining_3/spawn" to SlotIndexCondition(20),
    "crystal_hollows/entrance" to SlotIndexCondition(21),
    "combat_1/spawn" to SlotIndexCondition(22),
    "combat_3/spawn" to SlotIndexCondition(23),
    "crimson_isle/spawn" to SlotIndexCondition(24),
    "garden/spawn" to SlotIndexCondition(25),
    "rift/wizard_tower" to SlotIndexCondition(29),
    "fishing_1/spawn" to SlotIndexCondition(30),
    "lotus_atoll/spawn" to SlotIndexCondition(32),
    "winter/spawn" to SlotIndexCondition(33),
)

val unlockedCondition = SlotLoreCondition(EqualsTextMatcher("Warp not unlocked!"), Either.left(-1))

fun warp(name: String, island: String, id: String): List<GuiSlotDefinition> {
    return listOf(
        GuiSlotDefinition(
            of("skyblock_gui", "fast_travel/$island/${id}/locked"),
            SlotAllCondition(
                SlotNameCondition(EqualsTextMatcher(name)),
                unlockedCondition,
            ),
        ),
        GuiSlotDefinition(
            of("skyblock_gui", "fast_travel/$island/${id}/unlocked"),
            SlotAllCondition(
                SlotNameCondition(EqualsTextMatcher(name)),
                SlotNotCondition(unlockedCondition),
            ),
        ),
    )
}

fun warpMenu(
    title: String,
    island: String,
    warps: List<Pair<String, String>>,
) {
    val definition = GuiDefinition(
        target = GuiDefinitionTitleCondition(Regex(title)),
        layout = buildList {
            warps.forEach { (name, id) ->
                addAll(warp(name, island, id))
            }
        },
    )

    Path("repo/guis/fast_travel/$island.json").apply {
        createParentDirectories()
    }.writeText(definition.toJson(GuiDefinition.CODEC).prettyPrint())
}

fun mainMenu(
    vararg entries: Pair<String, TextMatcher>,
) {
    val definition = GuiDefinition(
        target = GuiDefinitionTitleCondition(Regex("Fast Travel")),
        layout = buildList {
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "fast_travel/island_browser"),
                    SlotNameCondition(EqualsTextMatcher("Island Browser")),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "fast_travel/advanced_mode/enabled"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher("Advanced Mode")),
                        SlotItemCondition(Items.DYE.lime),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "fast_travel/advanced_mode/disabled"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher("Advanced Mode")),
                        SlotItemCondition(Items.DYE.gray),
                    ),
                ),
            )

            for ((id, name) in entries) {
                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "fast_travel/${id}/undiscovered"),
                        SlotAllCondition(
                            SlotNameCondition(EqualsTextMatcher("Undiscovered Island")),
                            mainMenuSlotIndexes[id] ?: error("meow $id"),
                        ),
                    ),
                )

                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "fast_travel/${id}/locked"),
                        SlotAllCondition(
                            SlotNameCondition(name),
                            unlockedCondition,
                        ),
                    ),
                )

                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "fast_travel/${id}/unlocked"),
                        SlotAllCondition(
                            SlotNameCondition(name),
                            SlotNotCondition(unlockedCondition),
                        ),
                    ),
                )
            }
        },
    )

    Path("repo/guis/fast_travel/fast_travel.json").apply {
        createParentDirectories()
    }.writeText(definition.toJson(GuiDefinition.CODEC).prettyPrint())
}

fun warps() {
    mainMenu(
        "hub/spawn" to EqualsTextMatcher("SkyBlock Hub"),
        "dynamic/spawn" to EqualsTextMatcher("Private Island"),
        "dungeon_hub/spawn" to RegexTextMatcher("Dungeon Hub(?: - Spawn)?"),
        "farming_1/spawn" to RegexTextMatcher("The Barn(?: - Spawn)?"),
        "foraging_1/spawn" to RegexTextMatcher("The Park(?: - Spawn)?"),
        "foraging_2/spawn" to RegexTextMatcher("Galatea(?: - Spawn)?"),
        "mining_1/spawn" to RegexTextMatcher("Gold Mine(?: - Spawn)?"),
        "mining_2/spawn" to RegexTextMatcher("Deep Caverns(?: - Spawn)?"),
        "mining_3/spawn" to RegexTextMatcher("Dwarven Mines(?: - Spawn)?"),
        "crystal_hollows/entrance" to RegexTextMatcher("Crystal Hollows(?: - Entrance)?"),
        "combat_1/spawn" to RegexTextMatcher("Spider's Den(?: - Spawn)?"),
        "combat_3/spawn" to RegexTextMatcher("The End(?: - Spawn)?"),
        "crimson_isle/spawn" to RegexTextMatcher("Crimson Isle(?: - Spawn)?"),
        "garden/spawn" to EqualsTextMatcher("The Garden"),
        "rift/wizard_tower" to RegexTextMatcher("The Rift(?: - Wizard Tower)?"),
        "fishing_1/spawn" to RegexTextMatcher("Backwater Bayou(?: - Spawn)?"),
        "lotus_atoll/spawn" to RegexTextMatcher("Lotus Atoll(?: - Spawn)?"),
        "winter/spawn" to EqualsTextMatcher("Warp to: Jerry's Workshop"),
    )

    warpMenu(
        "Hub Warps",
        "hub",
        listOf(
            "SkyBlock Hub" to "spawn",
            "Hub - Castle" to "castle",
            "SkyBlock Hub - Elizabeth" to "elizabeth",
            "SkyBlock Hub - Sirius' Shack" to "sirius",
            "SkyBlock Hub - Crypts" to "crypts",
            "SkyBlock Hub - Museum" to "museum",
            "SkyBlock Hub - Taylor's Shop" to "taylor",
            "SkyBlock Hub - Wizard Tower" to "wizard",
            "Skyblock Hub - Carnival" to "carnival",
            "SkyBlock Hub - Trading Center" to "trading",
        ),
    )

    warpMenu(
        "The Farming Islands Warps",
        "farming_1",
        listOf(
            "The Barn - Spawn" to "spawn",
            "Mushroom Desert - Spawn" to "desert",
            "Mushroom Desert - Trapper's Den" to "trappers",
            "Mushroom Desert - Glowing Mushroom Cave" to "glowing_mushroom_cave",
        ),
    )

    warpMenu(
        "The Park Warps",
        "foraging_1",
        listOf(
            "The Park - Spawn" to "spawn",
            "The Park - Jungle" to "jungle",
            "The Park - Howling Cave" to "howling_cave",
        ),
    )

    warpMenu(
        "Galatea Warps",
        "foraging_2",
        listOf(
            "Galatea - Spawn" to "spawn",
            "Galatea - Murkwater Loch" to "murkwater",
        ),
    )

    warpMenu(
        "Dwarven Mines Warps",
        "mining_3",
        listOf(
            "Dwarven Mines - Spawn" to "spawn",
            "Dwarven Mines - Forge" to "forge",
            "Dwarven Mines - Base Camp" to "base_camp",
        ),
    )

    warpMenu(
        "Crystal Hollows Warps",
        "crystal_hollows",
        listOf(
            "Crystal Hollows - Entrance" to "entrance",
            "Crystal Hollows - Crystal Nucleus" to "crystal_nucleus",
        ),
    )

    warpMenu(
        "Spider's Den Warps",
        "combat_1",
        listOf(
            "Spider's Den - Spawn" to "spawn",
            "Spider's Den - Top of Nest" to "top_of_nest",
            "Spider's Den - Arachne's Sanctuary" to "arachne",
        ),
    )

    warpMenu(
        "The End Warps",
        "combat_3",
        listOf(
            "The End - Spawn" to "spawn",
            "The End - Dragon's Nest" to "nest",
            "The End - Void Sepulture" to "sepulture",
        ),
    )

    warpMenu(
        "Crimson Isle Warps",
        "crimson_isle",
        listOf(
            "Crimson Isle - Spawn" to "spawn",
            "Crimson Isle - Forgotten Skull" to "forgotten_skull",
            "Crimson Isle - The Wasteland" to "wasteland",
            "Crimson Isle - Dragontail" to "dragontail",
            "Crimson Isle - Scarleton" to "scarleton",
            "Crimson Isle - Smoldering Tomb" to "smoldering_tomb",
        ),
    )
}
