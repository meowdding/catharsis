package me.owdding.catharsis.features.text.targets

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.text.TextReplacements
import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack

@Module
object ItemTextReplacements : TextReplacements<ItemStack>("item") {

    init {
        Catharsis.registerClientReloadListener(Catharsis.id("text_replacements/item"), this)
    }
}
