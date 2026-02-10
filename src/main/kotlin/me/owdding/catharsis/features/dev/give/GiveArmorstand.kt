package me.owdding.catharsis.features.dev.give

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import me.owdding.catharsis.features.dev.GiveCommands
import me.owdding.catharsis.utils.types.commands.CommandFlag
import me.owdding.catharsis.utils.types.commands.FlagArgument
import me.owdding.catharsis.utils.types.commands.SkyBlockIdArgument
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.item.component.TypedEntityData
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.CommandBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.museum.MuseumData
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import tech.thatgravyboat.skyblockapi.utils.extentions.putCompound
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.wrap
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import java.util.*

@Module
object GiveArmorstand {


    enum class MannequinFlag(
        override val shortName: Char,
        longName: String? = null,
        override val group: String? = null,
        override val flagType: ArgumentType<*>? = null,
    ) : CommandFlag {
        IMMOVALBE('i'),
        STANDING('d', group = "pose"),
        CROUCHING('c', group = "pose"),
        SWIMMING('s', group = "pose"),
        FALL_FLYING('f', group = "pose"),
        SLEEPING('e', group = "pose"),
        LEFT_HANDED('l'),
        ;

        override val longName: String = longName ?: name.lowercase()
    }

    private val armorTypes = mutableSetOf(
        "HELMET",
        "MASK",
        "CHESTPLATE",
        "TUNIC",
        "LEGGINGS",
        "PANTS",
        "TROUSERS",
        "SLIPPERS",
        "BOOTS",
    )

    private val regex = "(?i)([\\s\\S]+?_)(?:${armorTypes.joinToString("|")})(_[\\s\\S]*)?$".toRegex()

    @Subscription
    private fun RegisterCommandsEvent.onRegister() {
        register("catharsis dev give") {
            then("armorstand") {
                createGive { tag, skyBlockId ->
                    Items.ARMOR_STAND.defaultInstance.apply {
                        set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.ARMOR_STAND, tag))
                        set(DataComponents.CUSTOM_NAME, Text.of(skyBlockId) { italic = false })
                    }
                }
            }

            fun createMannequin(flags: Set<MannequinFlag>, tag: CompoundTag, skyBlockId: String): ItemStack {
                return Items.FOX_SPAWN_EGG.defaultInstance.apply {
                    tag.putBoolean("hide_description", true)
                    when {
                        MannequinFlag.STANDING in flags -> tag.putString("pose", "standing")
                        MannequinFlag.CROUCHING in flags -> tag.putString("pose", "crouching")
                        MannequinFlag.SWIMMING in flags -> tag.putString("pose", "swimming")
                        MannequinFlag.FALL_FLYING in flags -> tag.putString("pose", "fall_flying")
                        MannequinFlag.SLEEPING in flags -> tag.putString("pose", "sleeping")
                    }
                    tag.putBoolean("immovable", MannequinFlag.IMMOVALBE in flags)
                    if (MannequinFlag.LEFT_HANDED in flags) tag.putString("main_hand", "left")
                    set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.MANNEQUIN, tag))
                    set(
                        DataComponents.CUSTOM_NAME,
                        Text.of(skyBlockId) {
                            italic = false
                            if (flags.isEmpty()) return@of
                            append(Text.join(flags.map { it.longName }, separator = Text.of(", ")).wrap(" (", ")"))
                        },
                    )
                    set(DataComponents.PROFILE, ResolvableProfile.createUnresolved(UUID.fromString("16102479-7162-4ea9-9975-a5059c6a2be3")))
                }
            }

            then("mannequin") {
                then("flag", FlagArgument.enum<MannequinFlag>()) {
                    createGive { tag, skyBlockId -> createMannequin(argument<Map<MannequinFlag, *>>("flag").keys, tag, skyBlockId) }
                }
                createGive { tag, skyBlockId -> createMannequin(emptySet(), tag, skyBlockId) }
            }
        }
    }

    private val ignoredCategories = mutableSetOf(
        "necklace",
        "cloak",
        "belt",
        "gloves",
        "bracelet",
    )

    private fun CommandBuilder<*>.createGive(itemConstructor: CommandContext<FabricClientCommandSource>.(CompoundTag, String) -> ItemStack) {
        thenCallback("id", SkyBlockIdArgument(SimpleItemAPI.getAllIds())) {
            val id = argument<SkyBlockId>("id")
            val skyBlockId = id.skyblockId

            val items = (MuseumData.museumData.armorSets.entries.find { skyBlockId in it.value }?.value?.map { RepoItemsAPI.getItem(it) } ?: run {
                // If museum data is not found, try to find items by matching stripped IDs

                armorTypes.mapNotNull { SkyBlockId.unknownType(skyBlockId.replace(regex, "$1$it$2"))?.toItem() }
            }).filterNot { it[DataTypes.CATEGORY]?.name in ignoredCategories }.associateBy { it.toSlotType() }

            val itemCompound = CompoundTag().apply {
                putCompound("equipment") {
                    put("head", (items[EquipmentSlot.HEAD] ?: ItemStack.EMPTY).toNBT())
                    put("chest", (items[EquipmentSlot.CHEST] ?: ItemStack.EMPTY).toNBT())
                    put("legs", (items[EquipmentSlot.LEGS] ?: ItemStack.EMPTY).toNBT())
                    put("feet", (items[EquipmentSlot.FEET] ?: ItemStack.EMPTY).toNBT())
                }
            }

            GiveCommands.tryGive(itemConstructor(itemCompound, skyBlockId))
        }
    }

    private fun ItemStack.toSlotType(): EquipmentSlot = this.get(DataComponents.EQUIPPABLE)?.slot ?: EquipmentSlot.HEAD
    private fun ItemStack.toNBT() = ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, this).orThrow
}
