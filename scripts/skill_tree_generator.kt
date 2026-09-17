import me.owdding.catharsis.features.gui.definitions.GuiDefinition
import me.owdding.catharsis.features.gui.definitions.conditions.GuiDefinitionTitleCondition
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.catharsis.features.gui.definitions.slots.SlotAllCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotAnyCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotItemModelCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotNameCondition
import me.owdding.catharsis.features.gui.matchers.EqualsTextMatcher
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.platform.Identifiers.of
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

fun hotmTree(
    title: String,
    perks: List<Pair<String, String>>,
) {
    val definition = GuiDefinition(
        target = GuiDefinitionTitleCondition(Regex(title)),
        layout = buildList {
            for ((name, id) in perks) {
                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "skill_tree/hotm/${id}/locked"),
                        SlotAllCondition(
                            SlotNameCondition(EqualsTextMatcher(name)),
                            SlotAnyCondition(
                                SlotItemModelCondition(Items.COAL),
                                SlotItemModelCondition(Items.COAL_BLOCK),
                            )
                        ),
                    ),
                )

                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "skill_tree/hotm/${id}/unlocked"),
                        SlotAllCondition(
                            SlotNameCondition(EqualsTextMatcher(name)),
                            SlotItemModelCondition(Items.EMERALD),
                        ),
                    ),
                )

                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "skill_tree/hotm/${id}/maxed"),
                        SlotAllCondition(
                            SlotNameCondition(EqualsTextMatcher(name)),
                            SlotItemModelCondition(Items.DIAMOND),
                        ),
                    ),
                )

                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "skill_tree/hotm/${id}/disabled"),
                        SlotAllCondition(
                            SlotNameCondition(EqualsTextMatcher(name)),
                            SlotItemModelCondition(Items.REDSTONE_BLOCK),
                        ),
                    ),
                )
            }
        },
    )

    Path("repo/guis/skill_tree/hotm.json").apply {
        createParentDirectories()
    }.writeText(definition.toJson(GuiDefinition.CODEC).prettyPrint())
}

fun hotfTree(
    title: String,
    perks: List<Pair<String, String>>,
) {
    val definition = GuiDefinition(
        target = GuiDefinitionTitleCondition(Regex(title)),
        layout = buildList {
            for ((name, id) in perks) {
                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "skill_tree/hotf/${id}/locked"),
                        SlotAllCondition(
                            SlotNameCondition(EqualsTextMatcher(name)),
                            SlotAnyCondition(
                                SlotItemModelCondition(Items.PALE_OAK_BUTTON),
                                SlotItemModelCondition(Items.PALE_OAK_SAPLING),
                            )
                        ),
                    ),
                )

                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "skill_tree/hotf/${id}/unlocked"),
                        SlotAllCondition(
                            SlotNameCondition(EqualsTextMatcher(name)),
                            SlotAnyCondition(
                                SlotItemModelCondition(Items.OAK_SAPLING),
                                SlotItemModelCondition(Items.STRIPPED_OAK_LOG),
                            )
                        ),
                    ),
                )

                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "skill_tree/hotf/${id}/maxed"),
                        SlotAllCondition(
                            SlotNameCondition(EqualsTextMatcher(name)),
                            SlotItemModelCondition(Items.OAK_LOG),
                        ),
                    ),
                )

                add(
                    GuiSlotDefinition(
                        of("skyblock_gui", "skill_tree/hotf/${id}/disabled"),
                        SlotAllCondition(
                            SlotNameCondition(EqualsTextMatcher(name)),
                            SlotItemModelCondition(Items.STRIPPED_MANGROVE_LOG),
                        ),
                    ),
                )
            }
        },
    )

    Path("repo/guis/skill_tree/hotf.json").apply {
        createParentDirectories()
    }.writeText(definition.toJson(GuiDefinition.CODEC).prettyPrint())
}

fun skillTrees() {
    hotmTree(
        "Heart of the Mountain",
        listOf(
            "Mining Speed" to "mining_speed",
            "Mining Speed Boost" to "mining_speed_boost",
            "Precision Mining" to "precision_mining",
            "Mining Fortune" to "mining_fortune",
            "Titanium Insanium" to "titanium_insanium",
            "Pickobulus" to "pickobulus",
            "Luck of the Cave" to "luck_of_the_cave",
            "Efficient Miner" to "efficient_miner",
            "Quick Forge" to "quick_forge",
            "Sky Mall" to "sky_mall",
            "Old-School" to "old_school",
            "Professional" to "professional",
            "Mole" to "mole",
            "Gem Lover" to "gem_lover",
            "Seasoned Mineman" to "seasoned_mineman",
            "Front Loaded" to "front_loaded",
            "Daily Grind" to "daily_grind",
            "Daily Powder" to "daily_powder",
            "Tunnel Vision" to "tunnel_vision",
            "Blockhead" to "block_head",
            "Subterranean Fisher" to "subterranean_fisher",
            "Keep It Cool" to "keep_it_cool",
            "Lonesome Miner" to "lonesome_miner",
            "Great Explorer" to "great_explorer",
            "Maniac Miner" to "maniac_miner",
            "Speedy Mineman" to "speedy_mineman",
            "Powder Buff" to "powder_buff",
            "Fortunate Mineman" to "fortunate_mineman",
            "Miner's Blessing" to "miners_blessing",
            "No Stone Unturned" to "no_stone_unturned",
            "Strong Arm" to "strong_arm",
            "Steady Hand" to "steady_hand",
            "Warm Heart" to "warm_heart",
            "Surveyor" to "surveyor",
            "Mineshaft Mayhem" to "mineshaft_mayhem",
            "Metal Head" to "metal_head",
            "Rags to Riches" to "rags_to_riches",
            "Eager Adventurer" to "eager_adventurer",
            "Gemstone Infusion" to "gemstone_infusion",
            "Crystalline" to "crystalline",
            "Gifts from the Departed" to "gifts_from_the_departed",
            "Mining Master" to "mining_master",
            "Dead Man's Chest" to "dead_mans_chest",
            "Vanguard Seeker" to "vanguard_seeker",
            "Sheer Force" to "sheer_forge"
        ),
    )

    hotfTree(
        "Heart of the Forest",
        listOf(
            "Sweep" to "sweep",
            "Damage Boost" to "damage_boost",
            "Luck of the Forest" to "luck_of_the_forest",
            "Foraging Fortune" to "foraging_fortune",
            "Collector" to "collector",
            "Axe Toss" to "axe_toss",
            "Deep Waters" to "deep_waters",
            "Hunter's Luck" to "hunters_luck",
            "Galatea's Might" to "galateas_might",
            "Lottery" to "lottery",
            "Foraging Madness" to "foraging_madness",
            "Iron Lungs" to "iron_lungs",
            "250 Gifts" to "250_gifts",
            "Daily Wishes" to "daily_wishes",
            "Early Bird" to "early_bird",
            "Precise Cutting" to "precise_cutting",
            "Tree Whisperer" to "tree_whisperer",
            "Free Trial" to "free_trial",
            "Homing Axe" to "homing_axe",
            "Forest Fisher" to "forest_fisher",
            "Strength Boost" to "strength_boost",
            "Starlyn Supreme" to "starlyn_supreme",
            "Speed Boost" to "speed_boost",
            "Efficient Forager" to "efficient_forager",
            "Maniac Slicer" to "maniac_slicer",
            "Half Empty" to "half_empty",
            "Ricochet" to "ricochet",
            "Half Full" to "half_full",
            "Monster Hunter" to "monster_hunter",
            "Forest Speed" to "forest_speed",
            "Essence Fortune" to "essence_fortune",
            "Timber" to "timber",
            "Two-for-one" to "two_for_one",
            "Forest Strength" to "forest_strength",
            "Beekeeper" to "beekeeper",
        ),
    )
}
