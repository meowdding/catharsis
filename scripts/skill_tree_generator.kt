import com.mojang.datafixers.util.Either
import me.owdding.catharsis.features.gui.definitions.GuiDefinition
import me.owdding.catharsis.features.gui.definitions.conditions.GuiDefinitionTitleCondition
import me.owdding.catharsis.features.gui.definitions.slots.GuiSlotDefinition
import me.owdding.catharsis.features.gui.definitions.slots.SlotAllCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotAnyCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotItemModelCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotLoreCondition
import me.owdding.catharsis.features.gui.definitions.slots.SlotNameCondition
import me.owdding.catharsis.features.gui.matchers.EqualsTextMatcher
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.platform.Identifiers.of
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

data class Icons(
    val perkLocked: List<Item>,
    val perkUnlocked: List<Item>,
    val perkMaxed: Item,
    val perkDisabled: Item,
    val coreLocked: Item
)

fun tree(
    title: String,
    treeId: String,
    icons: Icons,
    perks: Map<String, String>,
    maxLevel: Int,
    coreId: String,
    coreName: String,
    maxCore: Int,
    additionals: MutableList<GuiSlotDefinition>.() -> Unit,
) {
    fun MutableList<GuiSlotDefinition>.addPerks() {
        fun createItemCondition(items: List<Item>) = if (items.size == 1) {
            SlotItemModelCondition(items.first())
        } else {
            SlotAnyCondition(*items.map { SlotItemModelCondition(it) }.toTypedArray())
        }

        for ((name, id) in perks) {
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/$treeId/${id}/locked"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher(name)),
                        createItemCondition(icons.perkLocked),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/$treeId/${id}/unlocked"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher(name)),
                        createItemCondition(icons.perkUnlocked),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/$treeId/${id}/maxed"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher(name)),
                        SlotItemModelCondition(icons.perkMaxed),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/$treeId/${id}/disabled"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher(name)),
                        SlotItemModelCondition(icons.perkDisabled),
                    ),
                ),
            )
        }
    }

    fun MutableList<GuiSlotDefinition>.addLevels() {
        for (level in 1..maxLevel) {
            val name = "Tier $level"
            val id = "tier_$level"
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/$treeId/${id}/locked"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher(name)),
                        SlotItemModelCondition(Items.STAINED_GLASS_PANE.red),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/$treeId/${id}/unlocking"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher(name)),
                        SlotItemModelCondition(Items.STAINED_GLASS_PANE.yellow),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/$treeId/${id}/unlocked"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher(name)),
                        SlotItemModelCondition(Items.STAINED_GLASS_PANE.lime),
                    ),
                ),
            )
        }
    }

    fun MutableList<GuiSlotDefinition>.addCore() {
        for (level in 1..maxCore) {
            val id = "tier_$level"
            val lore = when {
                level == maxCore -> "Level $level"
                else -> "Level $level/$maxCore"
            }
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/$treeId/$coreId/unlocked/${id}"),
                    SlotAllCondition(
                        SlotNameCondition(EqualsTextMatcher(coreName)),
                        SlotLoreCondition(EqualsTextMatcher(lore), Either.left(0)),
                    ),
                ),
            )
        }
        add(
            GuiSlotDefinition(
                of("skyblock_gui", "skill_tree/$treeId/$coreId/locked"),
                SlotAllCondition(
                    SlotNameCondition(EqualsTextMatcher(coreName)),
                    SlotItemModelCondition(icons.coreLocked),
                ),
            ),
        )
    }

    val definition = GuiDefinition(
        target = GuiDefinitionTitleCondition(Regex(title)),
        layout = buildList {
            addPerks()
            addLevels()
            addCore()
            additionals()
        },
    )

    Path("repo/guis/skill_tree/$treeId.json").apply {
        createParentDirectories()
    }.writeText(definition.toJson(GuiDefinition.CODEC).prettyPrint())
}

@Suppress("DuplicatedCode")
fun skillTrees() {
    tree(
        title = "Heart of the Mountain",
        treeId = "hotm",
        icons = Icons(
            perkLocked = listOf(Items.COAL, Items.COAL_BLOCK),
            perkUnlocked = listOf(Items.EMERALD),
            perkMaxed = Items.DIAMOND,
            perkDisabled = Items.REDSTONE_BLOCK,
            coreLocked = Items.BEDROCK
        ),
        perks = mapOf(
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
            "Sheer Force" to "sheer_forge",
        ),
        maxLevel = 10,
        coreId = "core_of_the_mountain",
        coreName = "Core of the Mountain",
        maxCore = 10,
        additionals = {
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/hotm/loadout"),
                    SlotNameCondition(
                        EqualsTextMatcher("Heart of the Mountain Slot"),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/hotm/icon"),
                    SlotNameCondition(
                        EqualsTextMatcher("Heart of the Mountain"),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/hotm/crystal_hollows_crystals"),
                    SlotNameCondition(
                        EqualsTextMatcher("Crystal Hollows Crystals"),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/hotm/rng_meter"),
                    SlotNameCondition(
                        EqualsTextMatcher("Crystal Nucleus RNG Meter"),
                    ),
                ),
            )
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/hotm/reset_heart_of_the_mountain"),
                    SlotNameCondition(
                        EqualsTextMatcher("Reset Heart of the Mountain"),
                    ),
                ),
            )
        }
    )

    tree(
        title = "Heart of the Forest",
        treeId = "hotf",
        icons = Icons(
            perkLocked = listOf(Items.PALE_OAK_BUTTON, Items.PALE_OAK_SAPLING),
            perkUnlocked = listOf(Items.OAK_SAPLING, Items.STRIPPED_OAK_LOG),
            perkMaxed = Items.OAK_LOG,
            perkDisabled = Items.STRIPPED_MANGROVE_LOG,
            coreLocked = Items.MANGROVE_ROOTS
        ),
        perks = mapOf(
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
        maxLevel = 8,
        coreId = "center_of_the_forest",
        coreName = "Center of the Forest",
        maxCore = 5,
        additionals = {
            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/hotf/loadout"),
                    SlotNameCondition(
                        EqualsTextMatcher("Heart of the Forest Slot"),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/hotf/icon"),
                    SlotNameCondition(
                        EqualsTextMatcher("Heart of the Forest"),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/hotf/chapters/moonglade_marsh"),
                    SlotNameCondition(
                        EqualsTextMatcher("Moonglade Marsh"),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/hotf/chapters/torrhus_canyon"),
                    SlotNameCondition(
                        EqualsTextMatcher("Torrhus Canyon"),
                    ),
                ),
            )

            add(
                GuiSlotDefinition(
                    of("skyblock_gui", "skill_tree/hotf/reset_heart_of_the_forest"),
                    SlotNameCondition(
                        EqualsTextMatcher("Reset Heart of the Forest"),
                    ),
                ),
            )
        }
    )
}
