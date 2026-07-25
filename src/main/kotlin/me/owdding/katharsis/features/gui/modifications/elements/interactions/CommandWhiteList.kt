package me.owdding.katharsis.features.gui.modifications.elements.interactions

import me.owdding.katharsis.events.FinishRepoLoadEvent
import me.owdding.katharsis.events.StartRepoLoadEvent
import me.owdding.katharsis.generated.KatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData

@Module
object CommandWhiteList {
    private var cache: CommandConfig? = null

    @Subscription
    private fun StartRepoLoadEvent.start() {
        cache = null
    }

    @Subscription
    private fun FinishRepoLoadEvent.finish() {
        cache = getAsJson("commands.json")?.toData(CommandConfig.CODEC) ?: return
    }

    fun isWhitelisted(command: String): Boolean {
        val currentCache = cache ?: return false
        val baseCommand = command.trim().removePrefix("/")
        return currentCache.whitelist.any { baseCommand.startsWith(it, true) }
    }

    @GenerateCodec
    data class CommandConfig(
        val whitelist: Set<String>
    ) {
        companion object {
            val CODEC = KatharsisCodecs.getCodec<CommandConfig>()
        }
    }
}
