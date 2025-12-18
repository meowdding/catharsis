package me.owdding.catharsis.mixins.textures;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.owdding.catharsis.features.misc.AnimatableSimpleTexture;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {

    @Shadow
    public abstract void register(ResourceLocation path, AbstractTexture texture);

    @Shadow
    @Final
    private Set<Tickable> tickableTextures;

    @WrapOperation(
        method = "getTexture",
        at = @At(value = "NEW", target = "(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/texture/SimpleTexture;")
    )
    private SimpleTexture catharsis$onTextureCreate(ResourceLocation location, Operation<SimpleTexture> original) {
        return new AnimatableSimpleTexture(location);
    }

    @Definition(id = "Tickable", type = Tickable.class)
    @Expression("? instanceof Tickable")
    @WrapOperation(
        method = "register",
        at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean catharsis$isTickable(Object obj, Operation<Boolean> original) {
        if (obj instanceof AnimatableSimpleTexture texture) {
            return texture.getCanTick();
        }
        return original.call(obj);
    }

    @WrapOperation(
        method = "method_65880",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/ReloadableTexture;apply(Lnet/minecraft/client/renderer/texture/TextureContents;)V"
        )
    )
    private void catharsis$onTextureReloaded(ReloadableTexture instance, TextureContents contents, Operation<Void> original) {
        original.call(instance, contents);
        if (instance instanceof AnimatableSimpleTexture texture) {
            if (texture.getCanTick()) {
                this.tickableTextures.add(texture);
            } else {
                this.tickableTextures.remove(texture);
            }
        }
    }
}
