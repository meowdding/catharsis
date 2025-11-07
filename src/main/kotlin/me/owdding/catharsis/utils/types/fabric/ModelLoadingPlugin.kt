package me.owdding.catharsis.utils.types.fabric

import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

interface PreparingModelLoadingPlugin<T> : PreparableModelLoadingPlugin<T> {

    fun register() {
        PreparableModelLoadingPlugin.register(::prepare, this)
    }

    //? >= 1.21.9 {
    private fun prepare(sharedState: PreparableReloadListener.SharedState, executor: Executor): CompletableFuture<T> {
        return prepare(sharedState.resourceManager(), executor)
    }
    //?}

    fun prepare(resourceManager: ResourceManager, executor: Executor): CompletableFuture<T>
}
