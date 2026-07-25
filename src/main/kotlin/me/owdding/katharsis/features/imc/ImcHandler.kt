package me.owdding.katharsis.features.imc

import me.owdding.katharsis.features.gui.definitions.GuiDefinitions
import me.owdding.katharsis.features.gui.modifications.GuiModifiers
import me.owdding.katharsis.hooks.items.CustomDataHook
import me.owdding.katharsis.hooks.items.ItemStackHook
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Predicate

object ImcHandler {

    fun setup() {
        this.setup<Identifier>("item_id") { stack, id -> stack.withKatharsisId(id) }
        this.setup<Boolean>("disabled") { stack, disabled -> stack.withDisabled(disabled) }

        this.setup<Predicate<String>>("hidden_gui_elements", Predicate { element ->
            GuiModifiers.getActiveModifier()?.hiddenModElements?.contains(element) == true
        })
    }

    private fun <Data> setup(path: String, consumer: BiConsumer<ItemStack, Data>) {
        val invokers = runCatching { FabricLoader.getInstance().getEntrypoints("katharsis:imc/$path", Consumer::class.java) }
            .onFailure(Throwable::printStackTrace)
            .getOrDefault(listOf())

        for (invoker in invokers) {
            try {
                @Suppress("UNCHECKED_CAST")
                (invoker as Consumer<BiConsumer<ItemStack, Data>>).accept(consumer)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    private inline fun <reified T> setup(path: String, provider: T) {
        val invokers = runCatching { FabricLoader.getInstance().getEntrypoints("katharsis:imc/$path", Consumer::class.java) }
            .onFailure(Throwable::printStackTrace)
            .getOrDefault(emptyList())

        for (invoker in invokers) {
            try {
                @Suppress("UNCHECKED_CAST")
                (invoker as Consumer<T>).accept(provider)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun ItemStack.withKatharsisId(identifier: Identifier) {
        (this as ItemStackHook).`katharsis$setExtraId`(identifier)
    }

    @JvmStatic
    fun ItemStack.getKatharsisId(): Identifier? {
        val extraId = (this as ItemStackHook).`katharsis$getExtraId`()
        if (extraId != null) return extraId

        val hook = this.get(DataComponents.CUSTOM_DATA) as? CustomDataHook ?: return null
        return hook.`katharsis$getString`("katharsis:extra_id")?.let(Identifiers::parse)
    }

    fun ItemStack.withDisabled(disabled: Boolean) {
        (this as ItemStackHook).`katharsis$setDisabled`(disabled)
        GuiDefinitions.enqueueUpdate()
    }

    @JvmStatic
    fun ItemStack.isDisabled(): Boolean {
        val disabled = (this as ItemStackHook).`katharsis$isDisabled`()
        if (disabled) return true

        val hook = this.get(DataComponents.CUSTOM_DATA) as? CustomDataHook ?: return false
        return hook.`katharsis$getBoolean`("katharsis:disabled") ?: false
    }
}
