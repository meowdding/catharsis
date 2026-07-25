package me.owdding.katharsis.features.text.targets

import me.owdding.katharsis.Katharsis
import me.owdding.katharsis.features.text.TextReplacements
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component

@Module
object NametagTextReplacements : TextReplacements<Component>("nametag") {

    init {
        Katharsis.registerClientReloadListener(Katharsis.id("text_replacements/nametag"), this)
    }
}
