//~ named_identifier
package me.owdding.catharsis.mixins.pack;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import kotlin.Pair;
import me.owdding.catharsis.features.pack.config.PackConfigOption;
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
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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

    @Unique
    private static final int SIZE = 14;
    @Unique
    private static final Identifier COG_ICON = Identifier.fromNamespaceAndPath("catharsis", "cog");
    @Unique
    private static final Identifier COG_HIGHLIGHTED_ICON = Identifier.fromNamespaceAndPath("catharsis", "cog_highlighted");

    @Shadow
    @Final
    protected Minecraft minecraft;
    @Shadow
    @Final
    private PackSelectionModel.Entry pack;
    @Shadow
    @Final
    private StringWidget nameWidget;
    @Shadow
    @Final
    private MultiLineTextWidget descriptionWidget;

    @Unique
    private int right = 0;
    @Unique
    private int top = 0;

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
        var config = catharsis$getConfig();
        if (config == null || config.isEmpty()) return;

        int x = this.right - SIZE;
        int y = this.top;
        boolean buttonHovered = mouseX >= x && mouseX <= x + SIZE && mouseY >= y && mouseY <= y + SIZE;
        var icon = isHovering && buttonHovered ? COG_HIGHLIGHTED_ICON : COG_ICON;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon, x + 1, y + 1, SIZE - 2, SIZE - 2);

        if (buttonHovered) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
            graphics.setTooltipForNextFrame(this.minecraft.font, Component.literal("Configure Pack"), mouseX, mouseY);
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
        var config = catharsis$getConfig();
        var meta = catharsis$getMeta();
        if (config == null || meta == null) return;
        if (event.x() < this.right - SIZE || event.x() > this.right) {
            return;
        }
        if (event.y() < this.top || event.y() > this.top + SIZE) {
            return;
        }
        if (event.input() == InputConstants.MOUSE_BUTTON_LEFT && !config.isEmpty()) {
            this.minecraft.setScreen(new PackConfigScreen(this.minecraft.screen, meta.getId(), config));
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

    @Unique
    private List<PackConfigOption> catharsis$getConfig() {
        if (this.pack instanceof PackEntryHook hook) {
            return hook.catharsis$getConfig();
        }
        return null;
    }
}
