package me.owdding.catharsis.features.pack.config

import com.google.gson.JsonPrimitive
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.layouts.EqualSpacingLayout
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import tech.thatgravyboat.skyblockapi.utils.extentions.asBoolean

class PackConfigScreen(private val parent: Screen?, pack: String, private val options: List<PackConfigOption>) : Screen(Component.empty()) {

    private val layout = HeaderAndFooterLayout(this)
    private var scrollArea: ScrollableLayout? = null
    private val config = PackConfigHandler.getConfig(pack)
    private val originalConfigData = config.current.deepCopy()

    override fun init() {
        this.layout.headerHeight = 0

        val contents = LinearLayout.vertical().spacing(8)

        for (option in this.options) {
            contents.addChild(getOptionElement(option))
        }

        this.scrollArea = ScrollableLayout(this.minecraft!!, contents, 130)
        this.scrollArea!!.setMinWidth(310)
        this.layout.addToContents(this.scrollArea!!)

        val footer = this.layout.addToFooter<LinearLayout>(LinearLayout.horizontal())
        footer.addChild(Button.builder(CommonComponents.GUI_DONE) { this.onClose() }.build())

        this.layout.visitWidgets(this::addRenderableWidget)
        this.repositionElements()
    }

    override fun repositionElements() {
        this.scrollArea!!.setMaxHeight(130)
        this.layout.arrangeElements()
        val i = this.height - this.layout.footerHeight - this.scrollArea!!.rectangle.bottom()
        this.scrollArea!!.setMaxHeight(this.scrollArea!!.height + i)
    }

    override fun onClose() {
        this.minecraft!!.setScreen(this.parent)
        PackConfigHandler.save()
        if (this.config.current != this.originalConfigData) {
            this.minecraft!!.reloadResourcePacks()
        }
    }

    private fun getOptionElement(option: PackConfigOption): LayoutElement {
        val font = Minecraft.getInstance().font
        val line = EqualSpacingLayout(310, 0, EqualSpacingLayout.Orientation.HORIZONTAL)

        line.addChild(LinearLayout.vertical().spacing(4).apply {
            this.addChild(StringWidget(option.title, font))
            this.addChild(MultiLineTextWidget(option.description, font).apply {
                this.setColor(CommonColors.LIGHT_GRAY)
                this.setCentered(false)
                this.setMaxWidth(250)
            })
        })
        getOptionWidget(option)?.let(line::addChild)

        if (option is PackConfigOption.Separator) {
            return LinearLayout.vertical().apply {
                this.spacing(2)
                this.addChild(line)
                this.addChild(DividerElement(width = 310, height = 1))
            }
        }
        return line
    }

    private fun getOptionWidget(option: PackConfigOption): AbstractWidget? = when (option) {
        is PackConfigOption.Bool -> {
            val value = config.get(option.id).asBoolean(option.default)
            CycleButton.onOffBuilder(value).displayOnlyValue().create(0, 0, 44, 20, Component.empty()) { _, newValue ->
                config.set(option.id, JsonPrimitive(newValue))
            }
        }
        else -> null
    }
}
