package me.owdding.catharsis.compat

import io.github.fishstiz.packed_packs.api.PackedPacksApi
import io.github.fishstiz.packed_packs.api.PackedPacksInitializer
import io.github.fishstiz.packed_packs.api.events.InitializePackEntryEvent
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.pack.config.PackConfigScreen
import me.owdding.catharsis.hooks.pack.PackEntryHook
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.components.WidgetSprites

object PackedPacksCompat : PackedPacksInitializer {
    private const val BUTTON_SIZE = 14
    private val buttonSprites = WidgetSprites(Catharsis.id("cog"), Catharsis.id("cog_highlighted"))

    override fun onInitialize(api: PackedPacksApi) {
        val id = Catharsis.id("config_button")

        // TODO: Maybe add the hover/incompatibility tooltip
        api.eventBus().register(InitializePackEntryEvent::class.java, id) { event ->
            if (!event.screenContext().isClientResources || event.packContext().pack() !is PackEntryHook) return@register
            val config = event.packContext().pack().`catharsis$getConfig`()
            val meta = event.packContext().pack().`catharsis$getMetadata`()

            if (config == null || meta == null) return@register
            event.anchorTopRight(
                2, 2,
                ImageButton(0, 0, BUTTON_SIZE, BUTTON_SIZE, buttonSprites) { _ ->
                    if (!config.isEmpty()) {
                        Minecraft.getInstance().setScreen(PackConfigScreen(event.screenContext().screen(), meta.id, config))
                    }
                },
            )
        }
    }
}
