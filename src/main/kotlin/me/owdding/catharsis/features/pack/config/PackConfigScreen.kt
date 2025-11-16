package me.owdding.catharsis.features.pack.config

import com.google.gson.JsonPrimitive
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.components.tabs.Tab
import net.minecraft.client.gui.components.tabs.TabManager
import net.minecraft.client.gui.components.tabs.TabNavigationBar
import net.minecraft.client.gui.layouts.EqualSpacingLayout
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.extentions.asBoolean
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import java.util.function.Consumer
import kotlin.math.max


class PackConfigScreen(private val parent: Screen?, pack: String, private val options: List<PackConfigOption>) : Screen(Component.empty()) {

    private val layout = HeaderAndFooterLayout(this)
    private var scrollArea: ScrollableLayout? = null
    private val config = PackConfigHandler.getConfig(pack)
    private val originalConfigData = config.current.deepCopy()


    private val tabManager: TabManager = TabManager(
        { guieventlistener -> this.addRenderableWidget(guieventlistener) },
        { guieventlistener -> this.removeWidget(guieventlistener) },
    )
    private var tabNavigationBar: TabNavigationBar? = null

    override fun init() {
        val tabs: Map<Component, List<PackConfigOption>> = options.associate {
            when (it) {
                is PackConfigOption.Tab -> {
                    it.title to it.options
                }

                else -> Text.of("General") to options.filterNot { it is PackConfigOption.Tab }
            }
        }

        if (tabs.size > 1) {
            val tabTab = tabs.map { (title, options) ->
                PackConfigScreenTab(title, options)
            }
            this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width)
                .addTabs(*tabTab.toTypedArray())
                .build()
        }
        this.addRenderableWidget(this.tabNavigationBar)

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
        this.tabNavigationBar?.let {
            it.setWidth(this.width)
            it.arrangeElements()
            val i = it.rectangle.bottom()
            val screenrectangle = ScreenRectangle(0, i, this.width, this.height - this.layout.footerHeight - i)
            this.tabManager.setTabArea(screenrectangle)
            this.layout.headerHeight = i
            this.layout.arrangeElements()
        }

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

        line.addChild(
            LinearLayout.vertical().spacing(4).apply {
                this.addChild(StringWidget(option.title, font))
                this.addChild(
                    MultiLineTextWidget(option.description, font).apply {
                        this.active = true
                        this.setColor(CommonColors.LIGHT_GRAY)
                        this.setCentered(false)
                        this.setMaxWidth(225)
                        this.configureStyleHandling(true) {
                            it.clickEvent?.let { event ->
                                when (event) {
                                    is ClickEvent.OpenUrl -> Util.getPlatform().openUri(event.uri)
                                    is ClickEvent.CopyToClipboard -> McClient.clipboard = event.value
                                    else -> println("Cannot handle click event of type ${event.action()}")
                                }
                            }
                        }
                    },
                )
            },
        )
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

        is PackConfigOption.Dropdown -> {
            val value = config.get(option.id).asString()?.let { option.options.find { entry -> entry.value == it } } ?: option.default
            val width = max(option.options.maxOf { McFont.width(it.text) } + 8, 44)

            CycleButton.builder(PackConfigOption.Dropdown.Entry::text)
                .displayOnlyValue()
                .withValues(option.options)
                .withInitialValue(value)
                .create(0, 0, width, 20, Component.empty()) { _, entry ->
                    config.set(option.id, JsonPrimitive(entry.value))
                }
        }

        else -> null
    }
}

data class PackConfigScreenTab(
    val title: Component,
    val options: List<PackConfigOption>,
) : Tab {
    override fun getTabTitle(): Component = title

    override fun getTabExtraNarration(): Component? = null

    override fun visitChildren(consumer: Consumer<AbstractWidget>) {

    }

    override fun doLayout(rectangle: ScreenRectangle) {

    }
}
