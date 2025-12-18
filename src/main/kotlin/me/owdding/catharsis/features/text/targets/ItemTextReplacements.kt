package me.owdding.catharsis.features.text.targets

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.text.TextReplacements
import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.helpers.McClient

@Module
object ItemTextReplacements : TextReplacements<ItemStack>("item") {

    init {
        McClient.registerClientReloadListener(Catharsis.id("text_replacements/item"), this)
    }
}
