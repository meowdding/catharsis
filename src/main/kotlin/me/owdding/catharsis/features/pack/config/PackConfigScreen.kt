package me.owdding.catharsis.features.pack.config

import com.google.gson.JsonPrimitive
import me.owdding.catharsis.utils.extensions.CycleButtonBuilder
import me.owdding.catharsis.utils.extensions.withClickHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.*
import net.minecraft.client.gui.components.tabs.Tab
import net.minecraft.client.gui.components.tabs.TabManager
import net.minecraft.client.gui.components.tabs.TabNavigationBar
import net.minecraft.client.gui.layouts.*
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.util.Util
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.extentions.asBoolean
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import java.util.function.Consumer
import kotlin.math.max


class PackConfigScreen(private val parent: Screen?, pack: String, private val options: List<PackConfigOption>) : Screen(Component.empty()) {

    private val config = PackConfigHandler.getConfig(pack)
    private val originalConfigData = config.current.deepCopy()

    private val layout = HeaderAndFooterLayout(this)
    private val tabs: TabManager = TabManager({ widget -> this.addRenderableWidget(widget) }, { widget -> this.removeWidget(widget) })
    private var navigation: TabNavigationBar? = null

    override fun init() {
        val contents = mutableMapOf<Component, LinearLayout>()
        for (option in options) {
            when (option) {
                is PackConfigOption.Tab -> {
                    val layout = contents.getOrPut(option.title) { LinearLayout.vertical().spacing(8) }
                    option.options.map(this::getOptionElement).forEach(layout::addChild)
                }
                else -> {
                    val layout = contents.getOrPut(GENERAL_TAB) { LinearLayout.vertical().spacing(8) }
                    layout.addChild(this.getOptionElement(option))
                }
            }
        }

        this.navigation = this.addRenderableWidget(
            TabNavigationBar.builder(this.tabs, this.width)
                .addTabs(
                    *contents
                        .map { (title, layout) -> PackConfigScreenTab(title, layout) }
                        .sortedBy { tab -> if (tab.title == GENERAL_TAB) 0 else 1 }
                        .toTypedArray(),
                )
                .build(),
        )
        this.navigation!!.selectTab(0, false)

        val footer = this.layout.addToFooter<LinearLayout>(LinearLayout.horizontal())
        footer.addChild(Button.builder(CommonComponents.GUI_DONE) { this.onClose() }.build())

        this.layout.visitWidgets(this::addRenderableWidget)
        this.repositionElements()
    }

    override fun repositionElements() {
        val nav = this.navigation ?: return
        nav.setWidth(this.width)
        nav.arrangeElements()

        val navBottom = nav.rectangle.bottom()

        this.tabs.setTabArea(ScreenRectangle(0, navBottom, this.width, this.height - navBottom - this.layout.footerHeight))
        this.layout.headerHeight = navBottom
        this.layout.arrangeElements()
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
                    MultiLineTextWidget(Component.empty().append(option.description).withColor(CommonColors.LIGHT_GRAY), font).apply {
                        this.active = true
                        this.setCentered(false)
                        this.setMaxWidth(225)
                        this.withClickHandler {
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
            var value = config.get(option.id).asString()?.let { option.options.find { entry -> entry.value == it } } ?: option.default
            val width = max(option.options.maxOf { McFont.width(it.text) } + 8, 44)

            CycleButtonBuilder(PackConfigOption.Dropdown.Entry::text) { value }
                .displayOnlyValue()
                .withValues(option.options)
                .create(0, 0, width, 20, Component.empty()) { _, entry ->
                    value = entry
                    config.set(option.id, JsonPrimitive(entry.value))
                }
        }

        else -> null
    }

    companion object {

        private val GENERAL_TAB = Component.literal("General")
    }
}

class PackConfigScreenTab(val title: Component, contents: Layout) : Tab {

    val layout: ScrollableLayout = ScrollableLayout(McClient.self, contents, 130).also {
        it.setMinWidth(310)
        it.setMaxHeight(130)
    }

    override fun getTabTitle(): Component = title
    override fun getTabExtraNarration(): Component = Component.empty()
    override fun visitChildren(consumer: Consumer<AbstractWidget>) = layout.visitWidgets(consumer)
    override fun doLayout(rectangle: ScreenRectangle) {
        this.layout.setMaxHeight(rectangle.height - 20)
        this.layout.arrangeElements()
        FrameLayout.centerInRectangle(this.layout, rectangle)
        this.layout.y = rectangle.top() + 10
    }
}
