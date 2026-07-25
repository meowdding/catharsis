package me.owdding.katharsis.events

import me.owdding.katharsis.repo.KatharsisRemoteRepo
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

object StartRepoLoadEvent : SkyBlockEvent()
object FinishRepoLoadEvent : SkyBlockEvent() {
    fun getAsJson(file: String) = KatharsisRemoteRepo.getFileContentAsJson(file)
}
