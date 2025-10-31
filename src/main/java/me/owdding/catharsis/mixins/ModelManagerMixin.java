package me.owdding.catharsis.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.owdding.catharsis.features.models.BedrockModels;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

    @WrapMethod(method = "reload")
    public CompletableFuture<Void> reload(
        PreparableReloadListener.SharedState sharedState,
        Executor executor,
        PreparableReloadListener.PreparationBarrier preparationBarrier,
        Executor executor2,
        Operation<CompletableFuture<Void>> original
    ) {
        return BedrockModels.INSTANCE.reload(sharedState, executor, preparationBarrier, executor2).thenCompose(i ->
            original.call(sharedState, executor, preparationBarrier, executor2)
        );
    }

}
