package me.owdding.catharsis.compat

import com.mojang.blaze3d.platform.cursor.CursorTypes
import io.github.fishstiz.packed_packs.api.PackedPacksApi
import io.github.fishstiz.packed_packs.api.PackedPacksInitializer
import io.github.fishstiz.packed_packs.api.events.InitializePackEntryEvent
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.pack.config.PackConfigHandler
import me.owdding.catharsis.features.pack.config.PackConfigScreen
import me.owdding.catharsis.hooks.pack.PackEntryHook
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component

import java.util.Optional

object PackedPacksCompat : PackedPacksInitializer {
    private const val BUTTON_SIZE = 14
    val canEditConfigSprites = WidgetSprites(Catharsis.id("cog"), Catharsis.id("cog_highlighted"))
    val cantEditConfigSprites = WidgetSprites(Catharsis.id("cog_error"), Catharsis.id("cog_error"))
    val warningIncompatSprites = WidgetSprites(Catharsis.id("incompat_warning"), Catharsis.id("incompat_warning"))

    override fun onInitialize(api: PackedPacksApi) {
        val id = Catharsis.id("config_button")

        api.eventBus().register(InitializePackEntryEvent::class.java, id) { event ->
            if (!event.screenContext().isClientResources || event.packContext().pack() !is PackEntryHook) return@register
            val config = event.packContext().pack().`catharsis$getConfig`()
            val meta = event.packContext().pack().`catharsis$getMetadata`()

            if (config.isNullOrEmpty() && meta == null) return@register

            val requiresLoaded = event.packContext().pack().`catharsis$requiresPackToOpenConfig`()
            val canEdit = !requiresLoaded || PackConfigHandler.isLoaded(meta.id)

            if (!config.isNullOrEmpty()) {
                val configButton = object : ImageButton(
                    0, 0, BUTTON_SIZE, BUTTON_SIZE, canEditConfigSprites,
                    { _ ->
                        if (canEdit) {
                            Minecraft.getInstance().setScreen(PackConfigScreen(event.screenContext().screen(), meta.id, config))
                        }
                    },
                ) {
                    //~ if >= 26.1 'renderContents' -> 'extractContents'
                    override fun extractContents(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
                        if (isHovered) {
                            graphics.setTooltipForNextFrame(
                                Minecraft.getInstance().font,
                                (if (canEdit) Component.literal("Configure Pack") else Component.literal("Requires pack loaded to configure.").withStyle(ChatFormatting.RED)),
                                mouseX,
                                mouseY,
                            )
                        }
                        val sprite = (if (canEdit) canEditConfigSprites else cantEditConfigSprites).get(isActive, isHoveredOrFocused)
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height)
                    }

                    override fun handleCursor(graphics: GuiGraphicsExtractor) {
                        if (isHovered) {
                            graphics.requestCursor(if (canEdit) CursorTypes.POINTING_HAND else CursorTypes.NOT_ALLOWED)
                        }
                    }
                }
                event.anchorTopRight(2, 0, configButton)
            }
            if (meta !== null && !meta.incompatibilities.isEmpty()) {
                val incompatWidget = object : ImageButton(0, 0, BUTTON_SIZE, BUTTON_SIZE, warningIncompatSprites, {}) {
                    //~ if >= 26.1 'renderContents' -> 'extractContents'
                    override fun extractContents(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
                        val sprite = warningIncompatSprites.get(isActive, isHoveredOrFocused)
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height)
                        if (isHovered) {
                            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, meta.incompatibleTooltip, Optional.empty(), mouseX, mouseY)
                        }
                    }

                    override fun handleCursor(graphics: GuiGraphicsExtractor) {
                        if (isHovered) {
                            graphics.requestCursor(CursorTypes.ARROW)
                        }
                    }
                }
                event.anchorBottomRight(2, 0, incompatWidget)
            }
        }
    }
}
