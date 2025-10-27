package me.owdding.catharsis.utils.boundingboxes

import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent

interface DebugRenderable {

    fun render(event: RenderWorldEvent)

}
