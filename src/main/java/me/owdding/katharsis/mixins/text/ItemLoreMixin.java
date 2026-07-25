package me.owdding.katharsis.mixins.text;

import com.google.common.collect.Lists;
import me.owdding.katharsis.features.text.targets.ItemTextReplacements;
import me.owdding.katharsis.hooks.text.TooltipProviderHook;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemLore;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemLore.class)
public class ItemLoreMixin implements TooltipProviderHook {

    @Shadow @Final private static Style LORE_STYLE;
    @Shadow @Final private List<Component> lines;
    @Shadow @Final private List<Component> styledLines;

    @Unique private List<Component> katharsis$cachedLore = null;
    @Unique int katharsis$cacheKey = -1;
    @Unique boolean katharsis$unstyled = true;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("TAIL"))
    private void katharsis$storeUnstyledLines(List<Component> lines, CallbackInfo ci) {
        this.katharsis$unstyled = false;
    }

    @Override
    public void katharsis$addToTooltip(ItemStack stack, Item.TooltipContext context, Consumer<Component> adder, TooltipFlag flag, DataComponentGetter components) {
        katharsis$getOrCreateCache(stack).forEach(adder);
    }

    @Unique
    private List<Component> katharsis$getOrCreateCache(ItemStack stack) {
        var key = ItemTextReplacements.INSTANCE.getCacheKey();
        if (this.katharsis$cachedLore == null || this.katharsis$cacheKey != key) {
            this.katharsis$cacheKey = key;

            if (this.katharsis$unstyled) {
                // Means a mod or vanilla explicitly set the styled lore, so we can just replace directly.
                this.katharsis$cachedLore = ItemTextReplacements.INSTANCE.replace(stack, this.styledLines);
            } else {
                // Replacing on the unstyled lines and then applying the style is to workaround issues with mods like SkyHanni that
                // have a bug that will remove the dark purple but instead of only the MC lore style it also removes dark purple explicitly set.
                this.katharsis$cachedLore = Lists.transform(
                    ItemTextReplacements.INSTANCE.replace(stack, this.lines),
                    component -> ComponentUtils.mergeStyles(component.copy(), LORE_STYLE)
                );
            }
        }
        return this.katharsis$cachedLore;
    }
}
