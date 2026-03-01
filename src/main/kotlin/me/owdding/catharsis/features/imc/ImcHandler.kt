package me.owdding.catharsis.features.imc

import me.owdding.catharsis.hooks.items.CustomDataHook
import me.owdding.catharsis.hooks.items.ItemStackHook
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.platform.Identifiers

object ImcHandler {
    fun ItemStack.withCatharsisId(identifier: Identifier) {
        (this as ItemStackHook).`catharsis$setExtraId`(identifier)
    }

    @JvmStatic
    fun ItemStack.getCatharsisId(): Identifier? {
        val extraId = (this as ItemStackHook).`catharsis$getExtraId`()
        if (extraId != null) return extraId

        val hook = this.get(DataComponents.CUSTOM_DATA) as? CustomDataHook ?: return null
        return hook.`catharsis$getString`("catharsis:extra_id")?.let(Identifiers::parse)
    }
}
