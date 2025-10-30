package me.owdding.catharsis.utils

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
//? >= 1.21.9
import net.fabricmc.fabric.api.resource.v1.ResourceLoader

object Utils {

    fun resourceLocationWithDifferentFallbackNamespace(location: String, separator: Char, namespace: String): ResourceLocation {
        val i = location.indexOf(separator)
        return if (i >= 0) {
            val string = location.substring(i + 1)

            if (i != 0) {
                ResourceLocation.fromNamespaceAndPath(location.take(i), string)
            } else {
                ResourceLocation.fromNamespaceAndPath(namespace, string)
            }
        } else {
            ResourceLocation.fromNamespaceAndPath(namespace, location)
        }
    }

    //? < 1.21.9 {
    /*data class ReloadListenerWrapper(
        val id: ResourceLocation,
        val original: PreparableReloadListener,
    ) : IdentifiableResourceReloadListener {
        override fun getFabricId(): ResourceLocation = id

        override fun reload(
            barrier: PreparableReloadListener.PreparationBarrier,
            manager: ResourceManager,
            backgroundExecutor: Executor,
            gameExecutor: Executor,
        ): CompletableFuture<Void> = original.reload(barrier, manager, backgroundExecutor, gameExecutor)
    }
    *///?}

    fun registerClientReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
        //? >= 1.21.9 {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(id, listener)
        //?} else {
        /*ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ReloadListenerWrapper(id, listener))
        *///?}
    }
}
