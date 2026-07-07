package me.owdding.catharsis.features.text.targets

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.text.TextReplacements
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component

@Module
object NametagTextReplacements : TextReplacements<Component>("nametag") {

    init {
        Catharsis.registerClientReloadListener(Catharsis.id("text_replacements/nametag"), this)
    }
}
