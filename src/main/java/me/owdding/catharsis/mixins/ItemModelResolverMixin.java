package me.owdding.catharsis.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.hooks.ModelManagerHook;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypeItemStackKt;
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes;

import java.util.Locale;

@Mixin(ItemModelResolver.class)
public class ItemModelResolverMixin {

    @Unique
    private ModelManagerHook manager;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void catharsis$storeModelManager(ModelManager modelManager, CallbackInfo ci) {
        this.manager = modelManager instanceof ModelManagerHook hook ? hook : null;
    }

    @ModifyExpressionValue(method = "appendItemLayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private Object catharsis$modifyDataComponentType(Object original, @Local(argsOnly = true) ItemStack stack) {
        if (manager == null) return original;

        var id = DataTypeItemStackKt.getData(stack, DataTypes.INSTANCE.getAPI_ID());
        if (id == null) return original;

        id = id.replace(":", "-");

        var model = ResourceLocation.tryBuild("skyblock", id.toLowerCase(Locale.ROOT));
        if (model == null || !manager.catharsis$hasCustomModel(model)) return original;

        return model;
    }
}