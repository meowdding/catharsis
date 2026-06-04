package me.owdding.catharsis.features.entity

import me.owdding.catharsis.utils.debugToggle
import me.owdding.ktmodules.Module
import net.minecraft.gizmos.Gizmos
import net.minecraft.util.ARGB
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderEntityEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

@Module
object DebugEntityDefinition {

    private val debug by debugToggle("entity", "Show debug information for Entity definitions")

    @Subscription
    fun onRender(event: RenderEntityEvent) {
        if (!debug) return
        val entity = event.entity ?: return
        val definition = CustomEntityDefinitions.getFor(entity) ?: return
        Gizmos.billboardTextOverMob(event.entity, 2, "Entity Definition: $definition", ARGB.opaque(TextColor.WHITE), 0.32f)
    }
}
