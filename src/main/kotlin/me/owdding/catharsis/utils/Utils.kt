package me.owdding.catharsis.utils


//? >= 1.21.9
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener

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

    fun registerClientReloadListener(id: ResourceLocation, listener: PreparableReloadListener, second: ResourceLocation? = null) {
        //? >= 1.21.9 {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(id, listener)
        if (second != null) {
            ResourceLoader.get(PackType.CLIENT_RESOURCES).addReloaderOrdering(id, second)
        }
        //?} else {
        /*ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ReloadListenerWrapper(id, listener))
        *///?}
    }
}
