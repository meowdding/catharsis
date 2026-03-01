package me.owdding.catharsis.features.imc

import me.owdding.catharsis.hooks.items.CustomDataHook
import me.owdding.catharsis.hooks.items.ItemStackHook
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import java.util.function.BiConsumer
import java.util.function.Consumer

object ImcHandler {

    fun setup() {
        this.setup<Identifier>("item_id") { stack, id -> stack.withCatharsisId(id) }
        this.setup<Boolean>("disabled") { stack, disabled -> stack.withDisabled(disabled) }
    }

    private fun <Data> setup(path: String, consumer: BiConsumer<ItemStack, Data>) {
        val invokers = runCatching { FabricLoader.getInstance().getEntrypoints("catharsis:imc/$path", Consumer::class.java) }
            .onFailure(Throwable::printStackTrace)
            .getOrDefault(listOf())

        for (invoker in invokers) {
            try {
                (invoker as Consumer<BiConsumer<ItemStack, Data>>).accept(consumer)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun ItemStack.withCatharsisId(identifier: Identifier) {
        (this as ItemStackHook).`catharsis$setExtraId`(identifier)
    }

    @JvmStatic
    fun ItemStack.getCatharsisId(): Identifier? {
        val extraId = (this as ItemStackHook).`catharsis$getExtraId`()
        if (extraId != null) return extraId

        val hook = this.get(DataComponents.CUSTOM_DATA) as? CustomDataHook ?: return null
        return hook.`catharsis$getString`("catharsis:extra_id")?.let(Identifiers::parse)
    }

    fun ItemStack.withDisabled(disabled: Boolean) {
        (this as ItemStackHook).`catharsis$setDisabled`(disabled)
    }

    @JvmStatic
    fun ItemStack.isDisabled(): Boolean {
        val disabled = (this as ItemStackHook).`catharsis$isDisabled`()
        if (disabled) return true

        val hook = this.get(DataComponents.CUSTOM_DATA) as? CustomDataHook ?: return false
        return hook.`catharsis$getBoolean`("catharsis:disabled") ?: false
    }
}
