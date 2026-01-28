//~ named_identifier
package me.owdding.catharsis.mixins.items;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.owdding.catharsis.features.tooltip.TooltipFeature;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TooltipRenderUtil.class)
public class TooltipRenderUtilMixin {

    @WrapMethod(method = "getBackgroundSprite")
    private static Identifier catharsis$wrapGetBackgroundSprite(Identifier identifier, Operation<Identifier> original) {
        var custom = TooltipFeature.getBackground(identifier);
        return custom != null ? custom : original.call(identifier);
    }

    @WrapMethod(method = "getFrameSprite")
    private static Identifier catharsis$wrapGetFrameSprite(Identifier identifier, Operation<Identifier> original) {
        var custom = TooltipFeature.getFrame(identifier);
        return custom != null ? custom : original.call(identifier);
    }

}
