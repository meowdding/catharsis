package me.owdding.catharsis.utils

import me.owdding.catharsis.utils.colors.CatppuccinColors
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.ktmodules.Module
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.RepoAttributeAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.UNKNOWN
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.onClick

@Module
object ItemUtils {

    @Subscription
    private fun RegisterCommandsEvent.getMainHand() {
        registerWithCallback("catharsis dev hand_id") {
            val item = McPlayer.heldItem

            if (item.isEmpty) {
                Text.of("Not holding any item!").sendWithPrefix("catharsis-held-item-id-error")
            } else {
                val id = getCustomLocation(item)

                if (id == null) {
                    Text.of("Item has no custom id!").sendWithPrefix("catharsis-held-item-no-id")
                } else {
                    Text.of("Held item has id ") {
                        append(id.toString()) {
                            color = CatppuccinColors.Frappe.red
                        }
                        append("! ")

                        append("[COPY]") {
                            this.color = CatppuccinColors.Latte.lavender
                            onClick {
                                Text.of("Copied item id to clipboard!", CatppuccinColors.Frappe.yellow).sendWithPrefix("catharsis-held-item-copied-id")
                                McClient.clipboard = id.path
                            }
                        }
                    }.sendWithPrefix("catharsis-held-item-id-$id")
                }
            }
        }
    }

    fun getCustomLocation(item: ItemStack): ResourceLocation? {
        val itemId = item[DataTypes.SKYBLOCK_ID] ?: return null

        if (itemId.isItem) {
            val path = itemId.cleanId.lowercase().takeIf { ResourceLocation.isValidPath(it) } ?: return null
            return ResourceLocation.tryBuild("skyblock", path)
        }

        return when {
            itemId.isAttribute -> resolveAttribute(itemId)
            itemId.isRune -> resolveRune(itemId)
            itemId.isEnchantment -> resolveEnchantment(itemId)
            else -> ResourceLocation.tryBuild("unknown", itemId.cleanOrNull() ?: return null)
        }
    }

    private fun SkyBlockId.cleanOrNull() = this.cleanId.uppercase().takeUnless { it == UNKNOWN }

    fun resolveEnchantment(itemId: SkyBlockId): ResourceLocation? {
        val cleanId = itemId.cleanOrNull() ?: return null

        return ResourceLocation.tryBuild("skyblock", "enchantments/${cleanId.substringBefore(":").lowercase()}")
    }

    fun resolveRune(itemId: SkyBlockId): ResourceLocation? {
        val cleanId = itemId.cleanOrNull() ?: return null

        return ResourceLocation.tryBuild("skyblock", "runes/${cleanId.substringBefore(":").lowercase()}")
    }

    fun resolveAttribute(itemId: SkyBlockId): ResourceLocation? {
        val attributeId = itemId.cleanOrNull() ?: return null
        val data = RepoAttributeAPI.getAttributeDataById(attributeId) ?: return null
        return ResourceLocation.tryBuild("skyblock", "attributes/${data.shardId.lowercase()}")
    }

}
