package me.owdding.catharsis

import me.owdding.catharsis.features.properties.HoveredItemProperty
import me.owdding.catharsis.generated.CatharsisModules
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.ktmodules.Module
import net.fabricmc.api.ClientModInitializer
import net.minecraft.resources.ResourceLocation
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties
import net.minecraft.resources.ResourceLocation
import org.intellij.lang.annotations.Pattern
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

@Module
object Catharsis : ClientModInitializer, CatharsisLogger by CatharsisLogger.autoResolve() {
    const val MOD_ID = "catharsis"
    override fun onInitializeClient() {
        info("Catharsis client initialized!")
        CatharsisModules.init { SkyBlockAPI.eventBus.register(it) }

        ConditionalItemModelProperties.ID_MAPPER.put(HoveredItemProperty.ID, HoveredItemProperty.CODEC)
    }

    fun id(@Pattern("[a-z_0-9\\/.-]+") path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath("catharsis", path)
    fun sbapi(@Pattern("[a-z_0-9\\/.-]+") path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(SkyBlockAPI.MOD_ID, path)
}
