package me.owdding.catharsis.features.entity

import me.owdding.catharsis.utils.debugToggle
import me.owdding.ktmodules.Module
import net.minecraft.gizmos.Gizmos
import net.minecraft.gizmos.TextGizmo
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.render.RenderEntityEvent

@Module
object DebugEntityDefinition {

    private val debug by debugToggle("entity", "Show debug information for Entity definitions")

    @Subscription
    fun onRender(event: RenderEntityEvent) {
        if (!debug) return
        val entity = event.entity ?: return
        val definition = CustomEntityDefinitions.getFor(entity) ?: return
        val y = entity.y + 2.4 + 2 * 0.25
        val style = TextGizmo.Style.whiteAndCentered().withScale(0.32f)
        Gizmos.billboardText("Entity Definition: $definition", Vec3(entity.x, y, entity.z), style)
    }
}
