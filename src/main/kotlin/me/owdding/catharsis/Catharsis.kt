package me.owdding.catharsis

import net.fabricmc.api.ClientModInitializer

object Catharsis : ClientModInitializer {
    override fun onInitializeClient() {
        println("Catharsis client initialized!")
    }

}