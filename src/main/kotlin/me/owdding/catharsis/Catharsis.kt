package me.owdding.catharsis

import me.owdding.catharsis.generated.CatharsisModules
import me.owdding.catharsis.utils.CatharsisLogger
import me.owdding.ktmodules.Module
import net.fabricmc.api.ClientModInitializer
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

@Module
object Catharsis : ClientModInitializer, CatharsisLogger by CatharsisLogger.autoResolve() {
    override fun onInitializeClient() {
        info("Catharsis client initialized!")
        CatharsisModules.init { SkyBlockAPI.eventBus.register(this) }
    }

}