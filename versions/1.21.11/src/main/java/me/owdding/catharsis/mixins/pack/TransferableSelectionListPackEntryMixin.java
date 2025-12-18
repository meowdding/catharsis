package me.owdding.catharsis.mixins.pack;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import kotlin.Pair;
import me.owdding.catharsis.features.pack.config.PackConfigScreen;
import me.owdding.catharsis.features.pack.meta.CatharsisMetadataSection;
import me.owdding.catharsis.hooks.pack.PackEntryHook;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;


@Mixin(TransferableSelectionList.PackEntry.class)
public abstract class TransferableSelectionListPackEntryMixin extends ObjectSelectionList.Entry {

    @Shadow @Final protected Minecraft minecraft;
    @Shadow @Final private PackSelectionModel.Entry pack;
    @Shadow @Final private TransferableSelectionList parent;
    @Shadow @Final private StringWidget nameWidget;
    @Shadow @Final private MultiLineTextWidget descriptionWidget;

    @Unique private int right = 0;
    @Unique private int top = 0;

    @Inject(
        method = "renderContent",
        at = @At("HEAD")
    )
    private void renderConfigButton(CallbackInfo ci) {
        this.right = this.getContentRight();
        this.top = this.getContentY();
    }

    @Inject(
        method = "renderContent",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            ordinal = 1,
            target = "Lnet/minecraft/client/gui/components/MultiLineTextWidget;setMessage(Lnet/minecraft/network/chat/Component;)V"
        )
    )
    private void renderDescription(
        CallbackInfo ci,
        @Local(ordinal = 0, argsOnly = true) GuiGraphics graphics,
        @Local(ordinal = 0, argsOnly = true) int mouseX,
        @Local(ordinal = 1, argsOnly = true) int mouseY
    ) {
        var meta = catharsis$getMeta();
        List<Pair<String, ModContainer>> incompatibilities = meta != null ? meta.getIncompatibilities() : List.of();
        if (!incompatibilities.isEmpty()) {
            this.nameWidget.setMessage(Component.translatable("pack.catharsis.incompatible.title"));
            this.descriptionWidget.setMessage(Component.translatable("pack.catharsis.incompatible.desc"));
            graphics.setTooltipForNextFrame(this.minecraft.font, meta.getIncompatibleTooltip(), Optional.empty(), mouseX, mouseY);
        }
    }

    @Inject(
        method = "renderContent",
        at = @At("TAIL")
    )
    private void renderConfigButton(
        CallbackInfo ci,
        @Local(ordinal = 0, argsOnly = true) GuiGraphics graphics,
        @Local(ordinal = 0, argsOnly = true) int mouseX,
        @Local(ordinal = 1, argsOnly = true) int mouseY,
        @Local(ordinal = 0, argsOnly = true) boolean isHovering
    ) {
        var self = (TransferableSelectionList.PackEntry)(Object)this;
        var selected = this.parent.getSelected() == self;
        if (selected || isHovering) {
            var meta = catharsis$getMeta();
            if (meta == null || meta.getConfig().isEmpty()) return;
            int x = this.right - 11;
            int y = this.top;
            boolean buttonHovered = mouseX >= x && mouseX <= x + 11 && mouseY >= y && mouseY <= y + 11;
            graphics.fill(x, y, x + 11, y + 11, selected ? 0xff000000 : 0x88555555);
            graphics.drawString(this.minecraft.font, "⚙", x + 2, y + 1, buttonHovered ? 0xffB0B0B0 : 0xffffffff, false);
            if (buttonHovered) {
                graphics.requestCursor(com.mojang.blaze3d.platform.cursor.CursorTypes.POINTING_HAND);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
        var meta = catharsis$getMeta();
        if (meta == null) return;
        if (event.x() < this.right - 11 || event.x() > this.right) return;
        if (event.y() < this.top || event.y() > this.top + 11) return;
        if (event.input() == InputConstants.MOUSE_BUTTON_LEFT && !meta.getConfig().isEmpty()) {
            this.minecraft.setScreen(new PackConfigScreen(this.minecraft.screen, meta.getId(), meta.getConfig()));
            cir.setReturnValue(true);
        }
    }


    @Unique
    private CatharsisMetadataSection catharsis$getMeta() {
        if (this.pack instanceof PackEntryHook hook) {
            return hook.catharsis$getMetadata();
        }
        return null;
    }
}
