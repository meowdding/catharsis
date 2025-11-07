package me.owdding.catharsis.features.dev.give

import me.owdding.catharsis.features.dev.GiveCommands
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.types.colors.CatppuccinColors
import me.owdding.catharsis.utils.types.commands.SkyBlockIdArgument
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.museum.MuseumData
import tech.thatgravyboat.skyblockapi.utils.extentions.putCompound
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick

//? if > 1.21.8 {
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.item.component.TypedEntityData
import java.util.*
//?} else {
/*import net.minecraft.world.item.component.CustomData
*///?}

@Module
object GiveArmorstand {

    private val regex = "(?i)_(?:HELMET|MASK|CHESTPLATE|LEGGINGS|PANTS|BOOTS)$".toRegex()

    @Subscription
    private fun RegisterCommandsEvent.onRegister() {
        register("catharsis dev give") {
            then("armorstand", "mannequin") {

                val allIds = SimpleItemAPI.getAllIds()
                thenCallback("id", SkyBlockIdArgument(allIds)) {
                    val id = argument<SkyBlockId>("id")
                    val skyBlockId = id.skyblockId

                    val items = (MuseumData.museumData.armorSets.entries.find { skyBlockId in it.value }?.value?.map { RepoItemsAPI.getItem(it) } ?: run {
                        // If museum data is not found, try to find items by matching stripped IDs
                        val shortenedId = skyBlockId.replace(regex, "")
                        allIds.filter { it.skyblockId.replace(regex, "") == shortenedId }.map { it.toItem() }
                    }).associateBy { it.toSlotType() }

                    val itemCompound = CompoundTag().apply {
                        putCompound("equipment") {
                            put("head", (items[EquipmentSlot.HEAD] ?: ItemStack.EMPTY).toNBT())
                            put("chest", (items[EquipmentSlot.CHEST] ?: ItemStack.EMPTY).toNBT())
                            put("legs", (items[EquipmentSlot.LEGS] ?: ItemStack.EMPTY).toNBT())
                            put("feet", (items[EquipmentSlot.FEET] ?: ItemStack.EMPTY).toNBT())
                        }
                    }

                    //? if > 1.21.8 {
                    val armorStand = Items.ARMOR_STAND.defaultInstance.apply {
                        set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.ARMOR_STAND, itemCompound))
                        set(DataComponents.CUSTOM_NAME, Text.of(skyBlockId))
                    }
                    val mannequin = Items.FOX_SPAWN_EGG.defaultInstance.apply {
                        set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.MANNEQUIN, itemCompound))
                        set(DataComponents.CUSTOM_NAME, Text.of(skyBlockId))
                        set(DataComponents.PROFILE, ResolvableProfile.createUnresolved(UUID.fromString("16102479-7162-4ea9-9975-a5059c6a2be3")))
                    }
                    //?} else {
                    /*val armorStand = Items.ARMOR_STAND.defaultInstance.apply {
                        set(DataComponents.ENTITY_DATA, CustomData.of(itemCompound.apply {
                            putString("id", "minecraft:armor_stand")
                        }))
                        set(DataComponents.CUSTOM_NAME, Text.of(skyBlockId))
                    }
                    *///?}

                    Text.of("Give Armor for $skyBlockId: ") {
                        color = CatppuccinColors.Mocha.text
                        append("[Armor Stand]") {
                            onClick { GiveCommands.tryGive(armorStand) }
                            color = CatppuccinColors.Mocha.mauve
                        }
                        //? if > 1.21.8 {
                        append(" ")
                        append("[Mannequin]") {
                            onClick { GiveCommands.tryGive(mannequin) }
                            color = CatppuccinColors.Mocha.mauve
                        }
                        //?}
                    }.sendWithPrefix()
                }
            }
        }
    }

    private fun ItemStack.toSlotType(): EquipmentSlot = this.get(DataComponents.EQUIPPABLE)?.slot ?: EquipmentSlot.HEAD
    private fun ItemStack.toNBT() = ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, this).orThrow
}
