package me.owdding.catharsis.mixins.gui;

//~ gui_graphics

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import me.owdding.catharsis.features.gui.definitions.GuiDefinitions;
import me.owdding.catharsis.features.gui.modifications.GuiModifiers;
import me.owdding.catharsis.features.gui.modifications.modifiers.SlotModifier;
import me.owdding.catharsis.features.imc.ImcHandler;
import net.minecraft.Optionull;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.helpers.McFont;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenSlotsMixin<T extends AbstractContainerMenu> extends Screen {

    @Shadow
    @Final
    protected T menu;

    @Shadow
    protected int leftPos;

    @Shadow
    @Final
    protected int imageWidth;

    @Shadow
    protected int topPos;

    @Shadow
    @Final
    protected int imageHeight;

    protected AbstractContainerScreenSlotsMixin(Component title) {
        super(title);
    }

    @Unique
    private int catharsis$getCenterY() {
        return this.topPos + (this.imageHeight / 2);
    }

    @Unique
    private int catharsis$getCenterX() {
        return this.leftPos + (this.imageWidth / 2);
    }

    @Inject(method = "extractSlots", at = @At("HEAD"))
    private void catharsis$onRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        var modifier = GuiModifiers.getActiveModifier();
        for (var slot : this.menu.slots) {
            if (ImcHandler.isDisabled(slot.getItem())) {
                slot.catharsis$setPosition(null);
                slot.catharsis$setHighlightable(true);
                slot.catharsis$setHidden(false);
                continue;
            }
            var id = GuiDefinitions.getSlot(slot.index);
            var slotModifier = modifier != null && id != null ? modifier.getSlots().get(id) : null;

            slot.catharsis$setPosition(Optionull.map(slotModifier, SlotModifier::getPosition));
            slot.catharsis$setHidden(slotModifier != null && slotModifier.getHidden());
            slot.catharsis$setHighlightable(slotModifier == null || slotModifier.getHighlightable());
        }
        if (false && modifier != null && modifier.getBounds() != null) {
            var clickableBounds = modifier.getBounds();
            var x = catharsis$getCenterX() - clickableBounds.x / 2;
            var y = catharsis$getCenterY() - clickableBounds.y / 2;
            graphics.text(McFont.INSTANCE.getSelf(), mouseX + " - " + mouseY, 0, -30, (mouseX > x && mouseX < x + clickableBounds.x && mouseY > y && mouseY < y + clickableBounds.y) ? 0xFF00ff00 : 0xffff0000);
            graphics.text(McFont.INSTANCE.getSelf(), (x + clickableBounds.x) + " - " + (y + clickableBounds.y), 0, -20, -1);
            graphics.text(McFont.INSTANCE.getSelf(), x + " - " + y, 0, -10, -1);
        }
    }

    @ModifyReturnValue(method = "hasClickedOutside", at = @At("RETURN"))
    protected boolean catharsis$modifyHasClickedOutside(boolean original, @Local(argsOnly = true, ordinal = 0) double mouseX, @Local(argsOnly = true, ordinal = 1) double mouseY) {
        if (!original) return false;
        var modifier = GuiModifiers.getActiveModifier();
        if (modifier != null && modifier.getBounds() != null) {
            var clickableBounds = modifier.getBounds();
            var x = catharsis$getCenterX() - clickableBounds.x / 2;
            var y = catharsis$getCenterY() - clickableBounds.y / 2;
            return !(mouseX > x && mouseX < x + clickableBounds.x && mouseY > y && mouseY < y + clickableBounds.y);
        }
        return true;
    }

    @Mixin(AbstractRecipeBookScreen.class)
    public abstract static class AbstractRecipeScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreenSlotsMixin<T> {
        protected AbstractRecipeScreenMixin(Component title) {
            super(title);
        }

        @ModifyReturnValue(method = "hasClickedOutside", at = @At("RETURN"))
        protected boolean catharsis$modifyHasClickedOutside(boolean original, double mouseX, double mouseY) {
            return super.catharsis$modifyHasClickedOutside(original, mouseX, mouseY);
        }
    }

    @WrapMethod(method = "slotClicked")
    private void catharsis$onSlotClick(Slot slot, int slotId, int mouseButton, ContainerInput type, Operation<Void> original) {
        if (slot != null) {
            if (!ImcHandler.isDisabled(slot.getItem())) {
                var modifier = GuiModifiers.getActiveModifier();
                var id = GuiDefinitions.getSlot(slot.index);
                var slotModifier = modifier != null && id != null ? modifier.getSlots().get(id) : null;

                if (slotModifier != null && !slotModifier.getClickable()) return;
            }
        }
        original.call(slot, slotId, mouseButton, type);
    }
}
