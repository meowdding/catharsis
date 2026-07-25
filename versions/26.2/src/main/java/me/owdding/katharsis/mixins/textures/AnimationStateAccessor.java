package me.owdding.katharsis.mixins.textures;

import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteContents.AnimationState.class)
public interface AnimationStateAccessor {

    @Accessor("isDirty")
    boolean katharsis$isDirty();

}
