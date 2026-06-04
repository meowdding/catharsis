package me.owdding.catharsis.features.dev

import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.catharsis.utils.SkyBlockIdentifierResolver
import me.owdding.catharsis.utils.extensions.sendWithPrefix
import me.owdding.catharsis.utils.types.colors.CatppuccinColors
import me.owdding.catharsis.utils.types.suggestion.IterableSuggestionProvider
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object UntexturedIds {

    private val ids = SimpleItemAPI.getAllIds()

    private fun isIdTextured(itemId: SkyBlockId): Boolean {
        val id = SkyBlockIdentifierResolver.getCustomLocation(itemId)
        return McClient.self.modelManager.`catharsis$hasCustomModel`(id)
    }

    private enum class IdTypes(val condition: (SkyBlockId) -> Boolean = { true }) {
        ALL,
        ITEMS(SkyBlockId::isItem),
        ATTRIBUTES(SkyBlockId::isAttribute),
        ENCHANTMENTS(SkyBlockId::isEnchantment),
        RUNES(SkyBlockId::isRune),
        PETS(SkyBlockId::isPet),
        POTIONS(SkyBlockId::isPotion),
        ;
    }

    private fun checkIds(type: IdTypes = IdTypes.ALL) {
        val ids = if (type == IdTypes.ALL) ids else ids.filter { type.condition(it) }
        val untextured = ids.mapNotNull { it.takeUnless { isIdTextured(it) } }
        Text.of("Found ") {
            color = CatppuccinColors.Mocha.text
            append(untextured.size.toFormattedString(), CatppuccinColors.Mocha.lavender)
            append(" untextured ids for type ")
            append(type.name, CatppuccinColors.Mocha.blue)
            append("!")
        }.sendWithPrefix()
        McClient.clipboard = untextured.joinToString(", ") { it.cleanId }
    }

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("catharsis dev untextured_ids") {
            thenCallback("type", StringArgumentType.string(), IterableSuggestionProvider(IdTypes.entries, Enum<*>::name)) {
                checkIds(IdTypes.valueOf(argument<String>("type")))
            }
            callback { checkIds() }
        }
    }
}
