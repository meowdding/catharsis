package me.owdding.katharsis.features.pack.config

import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import me.owdding.katharsis.utils.extensions.CycleButtonBuilder
import me.owdding.katharsis.utils.extensions.withClickHandler
import me.owdding.katharsis.utils.ui.ResizingEqualSpacingLayout
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
import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors
import net.minecraft.util.Util
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.extentions.asBoolean
import tech.thatgravyboat.skyblockapi.utils.extentions.asInt
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.extentions.asStringList
import java.util.function.Consumer
import kotlin.math.max


class PackConfigScreen @JvmOverloads constructor(private val parent: Screen?, pack: String, private val options: List<PackConfigOption>, commandSearchQuery: String = "") :
    Screen(Component.empty()) {
    private val config = PackConfigHandler.getConfig(pack)
    private val originalConfigData = config.current.deepCopy()

    private val layout = HeaderAndFooterLayout(this)
    private val tabs: TabManager = TabManager({ widget -> this.addRenderableWidget(widget) }, { widget -> this.removeWidget(widget) })
    private var navigation: TabNavigationBar? = null

    private var searchQuery: String = commandSearchQuery
    private var searchBox: EditBox? = null
    private var currentTabTitle: Component? = null

    override fun init() {
        val contents = mutableMapOf<Component, LinearLayout>()

        for (option in options) {
            when (option) {
                is PackConfigOption.Tab -> {
                    val matchingOptions = option.options.filter { it.matches(searchQuery) }
                    if (matchingOptions.isNotEmpty()) {
                        val layout = contents.getOrPut(option.title(null)) { LinearLayout.vertical().spacing(8) }
                        matchingOptions.map(this::getOptionElement).forEach(layout::addChild)
                    }
                }

                else -> {
                    if (option.matches(searchQuery)) {
                        val layout = contents.getOrPut(GENERAL_TAB) { LinearLayout.vertical().spacing(8) }
                        layout.addChild(this.getOptionElement(option))
                    }
                }
            }
        }

        var sortedTabs = contents.map { (title, layout) -> PackConfigScreenTab(title, layout) }
            .sortedBy { tab -> if (tab.title == GENERAL_TAB) 0 else 1 }

        if (sortedTabs.isEmpty()) {
            val noResultsLayout = LinearLayout.vertical().spacing(8).apply {
                addChild(StringWidget(Component.literal("No results found."), font))
            }
            sortedTabs = listOf(PackConfigScreenTab(Component.literal("Search"), noResultsLayout))
        }

        this.navigation = this.addRenderableWidget(
            MinSizedTabNavigation(this.width, this.tabs, sortedTabs),
        )

        val tabToSelect = sortedTabs.indexOfFirst { it.title == currentTabTitle }.coerceAtLeast(0)
        if (sortedTabs.isNotEmpty()) {
            this.navigation!!.selectTab(tabToSelect, false)
        }

        this.searchBox = EditBox(McFont.self, 0, 0, 140, 20, Component.literal("Search..."))
        this.searchBox!!.value = searchQuery
        this.searchBox!!.setResponder { query ->
            if (this.searchQuery != query) {
                this.searchQuery = query
                this.currentTabTitle = this.tabs.currentTab?.tabTitle

                val wasFocused = this.focused == this.searchBox

                this.clearWidgets()
                this.init()

                if (wasFocused) {
                    this.focused = this.searchBox
                    this.searchBox!!.isFocused = true
                }
            }
        }

        val footer = this.layout.addToFooter<LinearLayout>(LinearLayout.horizontal().spacing(8))
        footer.addChild(this.searchBox!!)
        footer.addChild(Button.builder(CommonComponents.GUI_DONE) { this.onClose() }.build())

        this.layout.visitWidgets(this::addRenderableWidget)
        this.repositionElements()
    }

    override fun repositionElements() {
        val nav = this.navigation ?: return
        //? >= 26.2 {
        nav.arrangeElements(this.width)
        //?} else {
        /*nav.updateWidth(this.width)
        nav.arrangeElements()
         *///?}

        val navBottom = nav.rectangle.bottom()

        this.tabs.setTabArea(ScreenRectangle(0, navBottom, this.width, this.height - navBottom - this.layout.footerHeight))
        this.layout.headerHeight = navBottom
        this.layout.arrangeElements()
    }

    override fun onClose() {
        McClient.setScreen(this.parent)
        PackConfigHandler.save()
        if (this.config.current != this.originalConfigData) {
            this.minecraft!!.reloadResourcePacks()
        }
    }

    private fun handleComponentClick(handler: Style) {
        handler.clickEvent?.let { event ->
            when (event) {
                is ClickEvent.OpenUrl -> Util.getPlatform().openUri(event.uri)
                is ClickEvent.CopyToClipboard -> McClient.clipboard = event.value
                else -> println("Cannot handle click event of type ${event.action()}")
            }
        }
    }

    private fun getOptionElement(option: PackConfigOption): LayoutElement {
        val font = Minecraft.getInstance().font
        val line = ResizingEqualSpacingLayout.Horizontal(310)

        val titleWidget = StringWidget(option.title(null), font).apply {
            this.active = true
            this.withClickHandler(::handleComponentClick)
        }
        val descWidget = MultiLineTextWidget(Component.empty().append(option.description(null)).withColor(CommonColors.LIGHT_GRAY), font).apply {
            this.active = true
            this.setCentered(false)
            this.setMaxWidth(225)
            this.withClickHandler(::handleComponentClick)
        }

        line.addChild(
            LinearLayout.vertical().spacing(4).apply {
                this.addChild(titleWidget)
                this.addChild(descWidget)
            },
        )
        getOptionWidget(option, titleWidget, descWidget)?.let(line::addChild)

        if (option is PackConfigOption.Separator || option is PackConfigOption.Information) {
            return LinearLayout.vertical().apply {
                this.spacing(2)
                this.addChild(line)
                if (option is PackConfigOption.Separator) this.addChild(DividerElement(width = 310, height = 1))
            }
        }
        return line
    }

    private fun getOptionWidget(option: PackConfigOption, titleWidget: StringWidget, descWidget: MultiLineTextWidget): AbstractWidget? = when (option) {
        is PackConfigOption.Bool -> {
            val value = config.get(option.id).asBoolean(option.default)

            fun updateWidgets(value: Boolean) {
                titleWidget.message = option.title(value.toString())
                descWidget.text = option.description(value.toString())
            }

            updateWidgets(value)
            CycleButton.onOffBuilder(value).displayOnlyValue().create(0, 0, 44, 20, Component.empty()) { _, newValue ->
                config.set(option.id, JsonPrimitive(newValue))
                updateWidgets(newValue)
            }
        }

        is PackConfigOption.Dropdown -> {
            var value = config.get(option.id).asString()?.let { option.options.find { entry -> entry.value == it } } ?: option.default
            val width = max(option.options.maxOf { McFont.width(it.text) } + 8, 44)

            fun updateWidgets(entry: PackConfigOption.Dropdown.Entry) {
                titleWidget.message = option.title(entry.value)
                descWidget.text = option.description(entry.value)
            }

            updateWidgets(value)
            CycleButtonBuilder(PackConfigOption.Dropdown.Entry::text) { value }
                .displayOnlyValue()
                .withValues(option.options)
                .create(0, 0, width, 20, Component.empty()) { _, entry ->
                    value = entry
                    config.set(option.id, JsonPrimitive(entry.value))
                    updateWidgets(entry)
                }
        }

        is PackConfigOption.Select if option.single -> {
            val value = config.get(option.id).asString()?.let { option.options.find { entry -> entry.value == it } } ?: option.default.first()
            val width = max(option.options.maxOf { max(McFont.width(it.selectedText), McFont.width(it.unselectedText)) } + 8, 88)

            fun updateWidgets(entry: PackConfigOption.Select.SelectEntry) {
                titleWidget.message = option.title(entry.value)
                descWidget.text = option.description(entry.value)
            }

            updateWidgets(value)
            SelectButton<PackConfigOption.Select.SelectEntry>(width, 20).apply {
                this.singleValue = true
                this.onChange = { selected ->
                    config.set(option.id, JsonPrimitive(selected.first().value))
                    updateWidgets(selected.first())
                }

                for (entry in option.options) {
                    this.withEntry(entry, entry.selectedText, entry.unselectedText, entry == value)
                }
            }
        }

        is PackConfigOption.Select if true -> {
            val values = config.get(option.id).asStringList().mapNotNull { str -> option.options.find { it.value == str } }.toSet()
            val width = max(option.options.maxOf { max(McFont.width(it.selectedText), McFont.width(it.unselectedText)) } + 8, 88)

            SelectButton<PackConfigOption.Select.SelectEntry>(width, 20).apply {
                this.singleValue = false
                this.onChange = { selected ->
                    val json = JsonArray(selected.size)
                    selected.forEach { json.add(it.value) }
                    config.set(option.id, json)
                }

                for (entry in option.options) {
                    this.withEntry(entry, entry.selectedText, entry.unselectedText, entry in values)
                }
            }
        }

        is PackConfigOption.Color -> {
            val value = config.get(option.id).asInt(option.default)

            ColorPickerButton(76, 20, value, option.alpha) { newColor ->
                config.set(option.id, JsonPrimitive(newColor))
            }
        }

        else -> null
    }

    private var MultiLineTextWidget.text: Component
        get() = this.message
        set(value) {
            val height = this.height
            this.message = value
            if (height != this.height) {
                this@PackConfigScreen.repositionElements()
            }
        }

    private fun PackConfigOption.matches(query: String): Boolean {
        if (query.isBlank()) return true
        return this.title(null).string.contains(query, true) ||
            this.description(null).string.contains(query, true)
    }

    companion object {

        private val GENERAL_TAB = Component.literal("General")
    }
}

class PackConfigScreenTab(val title: Component, val contents: Layout) : Tab {

    val layout: ScrollableLayout = ScrollableLayout(McClient.self, contents, 130).also {
        it.setMinWidth(310)
        it.setMaxHeight(130)
    }

    //? >= 26.2
    override fun getLayout(): Layout = layout

    override fun getTabTitle(): Component = title
    override fun getTabExtraNarration(): Component = Component.empty()
    override fun visitChildren(consumer: Consumer<AbstractWidget>) = layout.visitWidgets(consumer)
    override fun doLayout(rectangle: ScreenRectangle) {
        this.contents.arrangeElements()
        this.contents.visitChildren { (it as? Layout)?.arrangeElements() }
        this.layout.setMaxHeight(rectangle.height - 20)
        this.layout.arrangeElements()
        FrameLayout.centerInRectangle(this.layout, rectangle)
        this.layout.y = rectangle.top() + 10
    }
}
