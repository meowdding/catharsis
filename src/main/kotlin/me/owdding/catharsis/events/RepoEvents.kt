package me.owdding.catharsis.events

import me.owdding.catharsis.repo.CatharsisRemoteRepo
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

object StartRepoLoadEvent : SkyBlockEvent()
object FinishRepoLoadEvent : SkyBlockEvent() {
    fun getAsJson(file: String) = CatharsisRemoteRepo.getFileContentAsJson(file)
}
