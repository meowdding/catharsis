package me.owdding.catharsis.mixins.text;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.owdding.catharsis.features.text.targets.NametagTextReplacements;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityNameTagMixin {
    @ModifyReturnValue(method = "getDisplayName", at = @At("RETURN"))
    private Component catharsis$modifyEntityNametag(Component original) {
        if (original == null) return null;
        return NametagTextReplacements.INSTANCE.replace(original, original);
    }

    @ModifyReturnValue(method = "getCustomName", at = @At("RETURN"))
    private Component catharsis$modifyEntityCustomName(Component original) {
        if (original == null) return null;
        return NametagTextReplacements.INSTANCE.replace(original, original);
    }
}
