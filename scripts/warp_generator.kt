import com.mojang.datafixers.util.Either
import me.owdding.catharsis.features.gui.definitions.GuiDefinition
import me.owdding.catharsis.features.gui.definitions.conditions.GuiDefinitionTitleCondition
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.catharsis.features.gui.definitions.slots.SlotAllCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotAnyCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotItemCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotLoreCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotNameCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotNotCondition
import me.owdding.catharsis.features.gui.matchers.EqualsTextMatcher
import me.owdding.catharsis.features.gui.matchers.RegexTextMatcher
import me.owdding.catharsis.features.gui.matchers.TextMatcher
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.platform.Identifiers.of
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

fun warp(name: String, island: String, id: String) = listOf(
    GuiSlotDefinition(
        of("skyblock_gui", "fast_travel/$island/${id}_locked"),
        SlotAllCondition(
            SlotNameCondition(EqualsTextMatcher(name)),
            SlotAnyCondition(
                SlotItemCondition(Items.BEDROCK),
                SlotLoreCondition(EqualsTextMatcher("Warp not unlocked!"), Either.left(-1)),
            ),
        ),
    ),
    GuiSlotDefinition(
        of("skyblock_gui", "fast_travel/$island/${id}_unlocked"),
        SlotAllCondition(
            SlotNameCondition(EqualsTextMatcher(name)),
            SlotNotCondition(SlotItemCondition(Items.BEDROCK)),
        ),
    ),
)

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
                    of("skyblock_gui", "fast_travel/paper_icons/enabled"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher("Paper Icons")),
                        SlotItemCondition(Items.FILLED_MAP),
                    ),
                ),
            )
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "fast_travel/paper_icons/disabled"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher("Paper Icons")),
                        SlotItemCondition(Items.MAP),
                    ),
                ),
            )
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "fast_travel/advanced_mode/enabled"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher("Advanced Mode")),
                        SlotItemCondition(Items.LIME_DYE),
                    ),
                ),
            )
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "fast_travel/advanced_mode/disabled"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher("Advanced Mode")),
                        SlotItemCondition(Items.GRAY_DYE),
                    ),
                ),
            )
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "fast_travel/undiscovered_island"),
                    SlotNameCondition(EqualsTextMatcher("Undiscovered Island")),
                ),
            )

            for ((id, name) in entries) {
                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "fast_travel/${id}_unlocked"),
                        SlotNameCondition(name),
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
        "private_island/spawn" to EqualsTextMatcher("Private Island"),
        "dungeon_hub/spawn" to RegexTextMatcher("Dungeon Hub(?: - Spawn)?"),
        "farming_islands/spawn" to RegexTextMatcher("The Barn(?: - Spawn)?"),
        "park/spawn" to RegexTextMatcher("The Park(?: - Spawn)?"),
        "galatea/spawn" to RegexTextMatcher("Galatea(?: - Spawn)?"),
        "gold_mine/spawn" to RegexTextMatcher("Gold Mine(?: - Spawn)?"),
        "deep_caverns/spawn" to RegexTextMatcher("Deep Caverns(?: - Spawn)?"),
        "dwarven_mines/spawn" to RegexTextMatcher("Dwarven Mines(?: - Spawn)?"),
        "crystal_hollows/entrance" to RegexTextMatcher("Crystal Hollows(?: - Entrance)?"),
        "spiders_den/spawn" to RegexTextMatcher("Spider's Den(?: - Spawn)?"),
        "the_end/spawn" to RegexTextMatcher("The End(?: - Spawn)?"),
        "crimson_isle/spawn" to RegexTextMatcher("Crimson Isle(?: - Spawn)?"),
        "garden/spawn" to EqualsTextMatcher("The Garden"),
        "rift/wizard_tower" to RegexTextMatcher("The Rift(?: - Wizard Tower)?"),
        "backwater_bayou/spawn" to RegexTextMatcher("Backwater Bayou(?: - Spawn)?"),
        "jerrys_workshop/spawn" to EqualsTextMatcher("Warp to: Jerry's Workshop"),
    )

    warpMenu(
        "The Farming Islands Warps",
        "farming_islands",
        listOf(
            "The Barn - Spawn" to "spawn",
            "Mushroom Desert - Spawn" to "desert",
            "Mushroom Desert - Trapper's Den" to "trapper",
        ),
    )

    warpMenu(
        "The Park Warps",
        "the_park",
        listOf(
            "The Park - Spawn" to "spawn",
            "The Park - Jungle" to "jungle",
            "The Park - Howling Cave" to "howling_cave",
        ),
    )

    warpMenu(
        "Galatea Warps",
        "galatea",
        listOf(
            "Galatea - Spawn" to "spawn",
            "Galatea - Murkwater Loch" to "murkwater",
        ),
    )

    warpMenu(
        "Dwarven Mines Warps",
        "dwarven_mines",
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
        "spiders_den",
        listOf(
            "Spider's Den - Spawn" to "spawn",
            "Spider's Den - Top of Nest" to "top_of_nest",
            "Spider's Den - Arachne's Sanctuary" to "arachne",
        ),
    )

    warpMenu(
        "The End Warps",
        "the_end",
        listOf(
            "The End - Spawn" to "spawn",
            "The End - Dragon's Nest" to "nest",
            "The End - Void Sepulture" to "sepulture"
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
            "Crimson Isle - Smoldering Tomb" to "smoldering_tomb"
        )
    )
}
