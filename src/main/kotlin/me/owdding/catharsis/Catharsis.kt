package me.owdding.catharsis

import me.owdding.catharsis.events.BootstrapConditionalPropertiesEvent
import me.owdding.catharsis.events.BootstrapNumericPropertiesEvent
import me.owdding.catharsis.events.BootstrapSelectPropertiesEvent
import me.owdding.catharsis.generated.CatharsisModules
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.ktmodules.Module
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties
import net.minecraft.resources.ResourceLocation
import org.intellij.lang.annotations.Pattern
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

@Module
object Catharsis : ClientModInitializer, CatharsisLogger by CatharsisLogger.autoResolve() {
    const val MOD_ID = "catharsis"
    override fun onInitializeClient() {
        info("Catharsis client initialized!")
        CatharsisModules.init { SkyBlockAPI.eventBus.register(it) }

        BootstrapConditionalPropertiesEvent(ConditionalItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapNumericPropertiesEvent(RangeSelectItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapSelectPropertiesEvent(SelectItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
    }

    fun id(@Pattern("[a-z_0-9\\/.-]+") path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath("catharsis", path)
    fun sbapi(@Pattern("[a-z_0-9\\/.-]+") path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(SkyBlockAPI.MOD_ID, path)
}
