package me.owdding.catharsis

import me.owdding.catharsis.events.BootstrapConditionalPropertiesEvent
import me.owdding.catharsis.events.BootstrapNumericPropertiesEvent
import me.owdding.catharsis.events.BootstrapSelectPropertiesEvent
import me.owdding.catharsis.generated.CatharsisModules
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.catharsis.utils.geometry.BakedBedrockGeometry
import me.owdding.catharsis.utils.geometry.BedrockGeometryBaker
import me.owdding.catharsis.utils.geometry.BedrockGeometryParser
import me.owdding.ktmodules.Module
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperties
import net.minecraft.resources.ResourceLocation
import org.intellij.lang.annotations.Pattern
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import java.nio.file.Files
import java.nio.file.Path

@Module
object Catharsis : ClientModInitializer, CatharsisLogger by CatharsisLogger.autoResolve() {

    @JvmStatic
    var model: BakedBedrockGeometry = loadModel()

    const val MOD_ID = "catharsis"
    override fun onInitializeClient() {
        info("Catharsis client initialized!")
        CatharsisModules.init { SkyBlockAPI.eventBus.register(it) }

        BootstrapConditionalPropertiesEvent(ConditionalItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapNumericPropertiesEvent(RangeSelectItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
        BootstrapSelectPropertiesEvent(SelectItemModelProperties.ID_MAPPER::put).post(SkyBlockAPI.eventBus)
    }

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.registerWithCallback("meow") {
            model = loadModel()
        }
    }

    fun loadModel(): BakedBedrockGeometry {
        val geo = BedrockGeometryParser.parse(
            Files.readString(Path.of("/mnt/drive2/git/vanity/example_pack/assets/example/geo/item/iron_armor.geo.json"))
        )

        return BedrockGeometryBaker.bake(geo.first())
    }

    fun id(@Pattern("[a-z_0-9\\/.-]+") path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath("catharsis", path)
    fun mc(@Pattern("[a-z_0-9\\/.-]+") path: String): ResourceLocation = ResourceLocation.withDefaultNamespace(path)
    fun sbapi(@Pattern("[a-z_0-9\\/.-]+") path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(SkyBlockAPI.MOD_ID, path)
}
