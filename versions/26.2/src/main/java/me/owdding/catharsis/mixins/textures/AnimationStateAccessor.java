package me.owdding.catharsis.mixins.textures;

import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteContents.AnimationState.class)
public interface AnimationStateAccessor {

    @Accessor("isDirty")
    boolean catharsis$isDirty();

}
