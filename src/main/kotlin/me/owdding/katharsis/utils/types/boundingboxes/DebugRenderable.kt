package me.owdding.katharsis.utils.types.boundingboxes

import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent

interface DebugRenderable {

    fun render(event: RenderWorldEvent)

}
