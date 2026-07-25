package me.owdding.katharsis.features.text.targets

import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.features.text.TextReplacements
import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.helpers.McClient

@Module
object ItemTextReplacements : TextReplacements<ItemStack>("item") {

    init {
        Katharsis.registerClientReloadListener(Katharsis.id("text_replacements/item"), this)
    }
}
