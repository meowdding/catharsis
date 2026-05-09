package me.owdding.catharsis.features.entity

import me.owdding.catharsis.utils.debugToggle
import me.owdding.ktmodules.Module
import net.minecraft.gizmos.Gizmos
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderEntityEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

@Module
object DebugEntityDefinition {

    private val debug by debugToggle("entity", "Show debug information for Entity definitions")

    @Subscription
    fun onRender(event: RenderEntityEvent) {
        val entity = event.entity ?: return
        val definition = CustomEntityDefinitions.getFor(entity) ?: return
        Gizmos.billboardTextOverMob(event.entity, 0, "Entity Definition: $definition", TextColor.DARK_PURPLE, 0.32f)
    }
}
