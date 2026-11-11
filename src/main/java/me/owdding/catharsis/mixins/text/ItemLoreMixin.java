package me.owdding.catharsis.mixins.text;

import com.google.common.collect.Lists;
import me.owdding.catharsis.features.text.targets.ItemTextReplacements;
import me.owdding.catharsis.hooks.text.TooltipProviderHook;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemLore;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique private List<Component> catharsis$cachedLore = null;
    @Unique int catharsis$cacheKey = -1;
    @Unique boolean catharsis$unstyled = true;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("TAIL"))
    private void catharsis$storeUnstyledLines(List<Component> lines, CallbackInfo ci) {
        this.catharsis$unstyled = false;
    }

    @Override
    public void catharsis$addToTooltip(ItemStack stack, Item.TooltipContext context, Consumer<Component> adder, TooltipFlag flag, DataComponentGetter components) {
        catharsis$getOrCreateCache(stack).forEach(adder);
    }

    @Unique
    private List<Component> catharsis$getOrCreateCache(ItemStack stack) {
        var key = ItemTextReplacements.INSTANCE.getCacheKey();
        if (this.catharsis$cachedLore == null || this.catharsis$cacheKey != key) {
            this.catharsis$cacheKey = key;

            if (this.catharsis$unstyled) {
                // Means a mod or vanilla explicitly set the styled lore, so we can just replace directly.
                this.catharsis$cachedLore = ItemTextReplacements.INSTANCE.replace(stack, this.styledLines);
            } else {
                // Replacing on the unstyled lines and then applying the style is to workaround issues with mods like SkyHanni that
                // have a bug that will remove the dark purple but instead of only the MC lore style it also removes dark purple explicitly set.
                this.catharsis$cachedLore = Lists.transform(
                    ItemTextReplacements.INSTANCE.replace(stack, this.lines),
                    component -> ComponentUtils.mergeStyles(component.copy(), LORE_STYLE)
                );
            }
        }
        return this.catharsis$cachedLore;
    }
}
