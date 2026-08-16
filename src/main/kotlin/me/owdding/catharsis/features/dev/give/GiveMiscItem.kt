package me.owdding.catharsis.features.dev.give

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import me.owdding.catharsis.features.dev.GiveCommands
import me.owdding.catharsis.features.item.MiscItemModels
import me.owdding.catharsis.utils.extensions.add
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.extensions.toVector3i
import me.owdding.catharsis.utils.extensions.unsafeCast
import me.owdding.catharsis.utils.types.Base64String
import me.owdding.catharsis.utils.types.colors.CatppuccinColors
import me.owdding.catharsis.utils.types.commands.CommandFlag
import me.owdding.catharsis.utils.types.commands.FlagArgument
import me.owdding.catharsis.utils.types.commands.IdentifierArgument
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
//? 26.2
import net.minecraft.world.entity.EntityTypes
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.item.component.TypedEntityData
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.CommandBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.createSkull
import tech.thatgravyboat.skyblockapi.utils.extentions.putCompound
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import java.util.UUID

@Module
object GiveMiscItem {


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

    val place_in_world = CommandFlag.of("place_in_world")


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

    fun Base64String.createData() = CompoundTag().apply {
        putCompound("equipment") {
            put("head", createSkull(this@createData).toNBT())
        }
    }

    @Subscription
    private fun RegisterCommandsEvent.onRegister() {
        register("catharsis dev give misc_item") {
            fun <Type : Entity> placeInWorld(list: List<Pair<TypedEntityData<EntityType<Type>>, (Type) -> Unit>>) {
                val server =
                    McClient.self.singleplayerServer ?: return Text.of("Not in singleplayer!", CatppuccinColors.Mocha.red).sendWithPrefix("catharsis-dev-give-no-singleplayer")

                val dimension =
                    McPlayer.self?.level()?.dimension() ?: return Text.of("Unable to find dimension!", CatppuccinColors.Mocha.red).sendWithPrefix("catharsis-dev-give-no-dimension")

                val level = server.getLevel(dimension) ?: return Text.of("Unable to find level!", CatppuccinColors.Mocha.red).sendWithPrefix("catharsis-dev-give-no-level")

                val blockPos = McPlayer.self?.blockPosition()!!
                val dir = McPlayer.self?.direction!!
                val vec = dir.unitVec3i.multiply(3)

                list.forEachIndexed { index, (creator, loader) ->
                    val center = blockPos.add(vec.multiply(index).toVector3i())
                    val entity = creator.type().create(level, EntitySpawnReason.COMMAND)!!
                    loader(entity)
                    level.addFreshEntity(entity)
                    entity.teleportTo(center.x + 0.5, center.y.toDouble(), center.z + 0.5)
                    for (x in -1..1) {
                        for (y in -1..1) {
                            level.setBlock(center.offset(x, -1, y), Blocks.SCULK.defaultBlockState(), Block.UPDATE_CLIENTS, 0)
                        }
                    }
                }
            }

            fun <Type : Entity> placeInWorld(list: List<TypedEntityData<EntityType<Type>>>) = placeInWorld(list.map { it to it::loadInto })

            fun createArmorStand(flags: Set<CommandFlag>, ids: Collection<Base64String>, id: Identifier): Collection<ItemStack> {
                val items = ids.map {
                    //~ if >= 26.2 'EntityType' -> 'EntityTypes'
                    TypedEntityData.of(EntityTypes.ARMOR_STAND, it.createData())
                }

                if (flags.contains(place_in_world)) {
                    placeInWorld(items)
                    return emptyList()
                }

                return items.map {
                    Items.FOX_SPAWN_EGG.defaultInstance.apply {
                        set(DataComponents.ENTITY_DATA, it.unsafeCast())
                        set(DataComponents.CUSTOM_NAME, Text.of(id.toString()) { italic = false })
                    }
                }
            }
            then("armorstand") {
                then("flag", FlagArgument(setOf(place_in_world))) {
                    createGive { tag, skyBlockId -> createArmorStand(argument<Map<CommandFlag, *>>("flag").keys, tag, skyBlockId) }
                }
                createGive { ids, id -> createArmorStand(emptySet(), ids, id) }
            }

            createGive { strings, identifier ->
                strings.mapIndexed { index, it ->
                    ItemBuilder(Items.PLAYER_HEAD) {
                        copyFrom(createSkull(it))
                        name(Text.of("$identifier ($index)"))
                    }
                }
            }

            fun createMannequin(flags: Set<CommandFlag>, tag: Collection<Base64String>, id: Identifier): Collection<ItemStack> {
                val items = tag.map {
                    val tag = it.createData()


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

                    //~ if >= 26.2 'EntityType' -> 'EntityTypes'
                    TypedEntityData.of(EntityTypes.MANNEQUIN, tag)
                }

                if (flags.contains(place_in_world)) {
                    placeInWorld(
                        items.map { entry ->
                            entry to {
                                entry.loadInto(it)
                                it.profile = ResolvableProfile.createUnresolved(UUID.fromString("16102479-7162-4ea9-9975-a5059c6a2be3"))
                            }
                        },
                    )
                    return emptyList()
                }

                return items.map {
                    Items.FOX_SPAWN_EGG.defaultInstance.apply {
                        set(DataComponents.ENTITY_DATA, it.unsafeCast())
                        set(DataComponents.CUSTOM_NAME, Text.of(id.toString()) { italic = false })
                    }
                }
            }

            then("mannequin") {
                then("flag", FlagArgument(MannequinFlag.entries + place_in_world)) {
                    createGive { tag, skyBlockId -> createMannequin(argument<Map<CommandFlag, *>>("flag").keys, tag, skyBlockId) }
                }
                createGive { tag, skyBlockId -> createMannequin(emptySet(), tag, skyBlockId) }
            }
        }
    }

    private fun CommandBuilder<*>.createGive(itemConstructor: CommandContext<FabricClientCommandSource>.(Collection<Base64String>, Identifier) -> Collection<ItemStack>) {
        thenCallback("id", IdentifierArgument({ MiscItemModels.collectItems().keySet() })) {
            val id = argument<Identifier>("id")

            val ids = MiscItemModels.collectItems().get(id)

            val collection = itemConstructor(ids, id)
            if (collection.size < 5) {
                collection.forEach(GiveCommands::tryGive)
            } else {
                GiveCommands.fillAndGiveShulkers(collection.toList())
            }
        }
    }

    private fun ItemStack.toSlotType(): EquipmentSlot = this.get(DataComponents.EQUIPPABLE)?.slot ?: EquipmentSlot.HEAD
    private fun ItemStack.toNBT() = ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, this).orThrow

}
