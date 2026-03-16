package me.owdding.catharsis.features.pack.config

import com.mojang.blaze3d.platform.NativeImage
import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.features.pack.config.PackConfigHandler.CatharsisPack
import net.minecraft.client.gui.components.ImageWidget
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.repository.Pack
import net.minecraft.util.Util
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

class PackConfigsScreen(val packs: List<CatharsisPack>) : Screen(Text.of("Catharsis Pack Configurations")) {

    private val packIcons: MutableMap<String, Identifier> = mutableMapOf()
    private val DEFAULT_ICON: Identifier = Identifier.withDefaultNamespace("textures/misc/unknown_pack.png")

    override fun init() {
        val vertical = LinearLayout.vertical()

        packs.forEach { pack ->
            val horizontal = LinearLayout.horizontal().apply {
                addChild(ImageWidget.texture(32, 32, getPackIcon(pack.pack), 32, 32))
                addChild(SpacerElement(5, 0))
                addChild(
                    LinearLayout.vertical().apply {
                        addChild(MultiLineTextWidget(pack.pack.title, McFont.self))
                        addChild(
                            MultiLineTextWidget(ComponentUtils.mergeStyles(pack.pack.description.copy(), Style.EMPTY.withColor(TextColor.GRAY)), McFont.self).apply {
                                setMaxWidth(300)
                                setMaxRows(2)
                            },
                        )
                    },
                )
            }
            vertical.addChild(horizontal)
            vertical.addChild(SpacerElement(0, 20))
        }

        vertical.arrangeElements()
        FrameLayout.centerInRectangle(vertical, 0, 0, this.width, this.height)

        vertical.visitWidgets { addRenderableWidget(it) }
    }


    private fun getPackIcon(pack: Pack): Identifier = packIcons.getOrPut(pack.id) { loadPackIcon(pack) }
    private fun loadPackIcon(pack: Pack): Identifier = runCatching {
        pack.open().use { packResources ->
            val resource = packResources.getRootResource("pack.png") ?: return DEFAULT_ICON

            val location = Catharsis.id("pack/" + Util.sanitizeName(pack.id, Identifier::validPathChar) + "/icon")
            resource.get().use { stream ->
                McClient.self.textureManager.register(location, DynamicTexture(location::toString, NativeImage.read(stream)))
                location
            }
        }
    }.getOrElse { ex ->
        Catharsis.warn("Failed to load pack icon ${pack.id}", ex)
        DEFAULT_ICON
    }
}
