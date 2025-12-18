package me.owdding.catharsis.features.dev

import com.google.gson.JsonParser
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.MultiLineEditBox
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.bold
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.underlined

class RegexTester : Screen(CommonComponents.EMPTY) {

    private var input: MultiLineEditBox? = null
    private var output: MultiLineTextWidget? = null

    private var regex: EditBox? = null

    override fun init() {
        val boxWidth = this.width / 2 - 30
        val boxHeight = this.height - 100

        this.input = this.addRenderableWidget(MultiLineEditBox.builder()
            .setPlaceholder(Text.of("Text Component Input"))
            .build(this.font, boxWidth, boxHeight, CommonText.EMPTY)
        )
        this.input?.setSize(boxWidth, boxHeight)
        this.input?.value = inputText

        this.output = this.addRenderableWidget(MultiLineTextWidget(
            Text.of("Output will appear here") { this.color = 0xCCE0E0E0.toInt() },
            this.font
        ))
        this.output?.setSize(boxWidth, boxHeight)
        this.output?.setCentered(false)
        this.output?.message = outputText

        this.input!!.setPosition(10, 10)
        this.output!!.setPosition(this.width - boxWidth - 10, 10)

        this.regex = this.addRenderableWidget(EditBox(
            this.font,
            this.width / 2 - 100, this.height - 30,
            200, 20,
            Text.of("Regex Pattern")
        ))
        this.regex?.setMaxLength(65536)
        this.regex?.value = regexText

        this.regex?.setResponder { this.updateOutput() }
        this.input?.setValueListener { this.updateOutput() }
    }

    private fun setErrorMessage(message: String) {
        this.output?.message = Text.of(message) {
            this.color = TextColor.RED
        }
    }

    private fun updateOutput() {
        val regex = runCatching { ComponentRegex(this.regex!!.value) }
            .onFailure { setErrorMessage("Invalid regex pattern: ${it.message}") }
            .getOrNull() ?: return
        val component = runCatching { JsonParser.parseString(this.input!!.value).toDataOrThrow(ComponentSerialization.CODEC) }
            .getOrElse { Text.of(this.input?.value ?: "") }

        runCatching {
            this.output?.message = regex.replace(component) { match ->
                Text.of(match.value().stripped) {
                    this.color = TextColor.YELLOW
                    this.bold = true
                    this.underlined = true
                }
            }
        }.onFailure {
            setErrorMessage("Error processing input: ${it.message}")
        }

        outputText = this.output?.message ?: Text.of("")
        inputText = this.input?.value ?: ""
        regexText = this.regex?.value ?: ""
    }

    @Module
    companion object {

        private var regexText: String = ""
        private var inputText: String = ""
        private var outputText: Component = Text.of("")

        @Subscription
        private fun RegisterCommandsEvent.register() {
            registerWithCallback("catharsis dev regex") {
                McClient.setScreenAsync { RegexTester() }
            }
        }
    }
}
