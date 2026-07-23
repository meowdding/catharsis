package me.owdding.catharsis.mixins.pack;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import kotlin.Pair;
import me.owdding.catharsis.features.pack.config.PackConfigHandler;
import me.owdding.catharsis.features.pack.config.PackConfigOption;
import me.owdding.catharsis.features.pack.config.PackConfigScreen;
import me.owdding.catharsis.features.pack.meta.CatharsisMetadataSection;
import me.owdding.catharsis.hooks.pack.PackEntryHook;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.PackCompatibility;
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
    @Unique
    private static final Identifier COG_ERROR_ICON = Identifier.fromNamespaceAndPath("catharsis", "cog_error");

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
        method = "extractContent",
        at = @At("HEAD")
    )
    private void renderConfigButton(CallbackInfo ci) {
        this.right = this.getContentRight();
        this.top = this.getContentY();
    }

    @ModifyExpressionValue(
        method = "extractContent",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackCompatibility;isCompatible()Z", ordinal = 0)
    )
    private boolean expandIncompatibleCheck(boolean isCompatible) {
        if (!isCompatible) return false;
        var meta = catharsis$getMeta();
        return meta == null || meta.getIncompatibilities().isEmpty();
    }

    @Inject(
        method = "extractContent",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            ordinal = 1,
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"
        )
    )
    private void renderDescription(
        CallbackInfo ci,
        @Local(ordinal = 0, argsOnly = true) GuiGraphicsExtractor graphics,
        @Local(ordinal = 0, argsOnly = true) int mouseX,
        @Local(ordinal = 1, argsOnly = true) int mouseY
    ) {
        var meta = catharsis$getMeta();
        List<Pair<String, ModContainer>> incompatibilities = meta != null ? meta.getIncompatibilities() : List.of();
        if (!incompatibilities.isEmpty()) {
            this.nameWidget.setMessage(Component.translatable("pack.catharsis.incompatible.title"));
            this.descriptionWidget.setMessage(Component.translatable("pack.catharsis.incompatible.desc").withStyle(ChatFormatting.GRAY));
            PackCompatibility compatibility = this.pack.getCompatibility();
            if (compatibility.isCompatible() && this.descriptionWidget.isHovered()) {
                graphics.setTooltipForNextFrame(this.minecraft.font, meta.getIncompatibleTooltip(), Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Inject(
        method = "extractContent",
        at = @At("TAIL")
    )
    private void renderConfigButton(
        CallbackInfo ci,
        @Local(ordinal = 0, argsOnly = true) GuiGraphicsExtractor graphics,
        @Local(ordinal = 0, argsOnly = true) int mouseX,
        @Local(ordinal = 1, argsOnly = true) int mouseY,
        @Local(ordinal = 0, argsOnly = true) boolean isHovering
    ) {
        var config = catharsis$getConfig();
        if (config == null || config.isEmpty()) return;

        int x = this.right - SIZE + 2;
        int y = this.top - 2;
        boolean buttonHovered = mouseX >= x && mouseX <= x + SIZE && mouseY >= y && mouseY <= y + SIZE;
        boolean canEdit = this.catharsis$canEditPack();

        var icon = isHovering && buttonHovered ? canEdit ? COG_HIGHLIGHTED_ICON : COG_ERROR_ICON : canEdit ? COG_ICON : COG_ERROR_ICON;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon, x + 1, y + 1, SIZE - 2, SIZE - 2);

        if (buttonHovered) {
            if (canEdit) {
                graphics.requestCursor(CursorTypes.POINTING_HAND);
                graphics.setTooltipForNextFrame(this.minecraft.font, Component.literal("Configure Pack"), mouseX, mouseY);
            } else {
                graphics.requestCursor(CursorTypes.NOT_ALLOWED);
                graphics.setTooltipForNextFrame(this.minecraft.font, Component.literal("Requires pack loaded to configure.").withStyle(ChatFormatting.RED), mouseX, mouseY);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
        var config = catharsis$getConfig();
        var meta = catharsis$getMeta();
        if (config == null || meta == null || !this.catharsis$canEditPack()) return;
        if (event.x() < this.right - SIZE || event.x() > this.right) {
            return;
        }
        if (event.y() < this.top || event.y() > this.top + SIZE) {
            return;
        }
        if (event.input() == InputConstants.MOUSE_BUTTON_LEFT && !config.isEmpty()) {
            //~ if >=26.2 '.minecraft' -> '.minecraft.gui'  {
            //~ if >=26.2 '.screen' -> '.screen()'
            this.minecraft.gui.setScreen(new PackConfigScreen(this.minecraft.gui.screen(), meta.getId(), config));
            //~}
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

    @Unique
    private boolean catharsis$canEditPack() {
        if (this.pack instanceof PackEntryHook hook) {
            if (hook.catharsis$requiresPackToOpenConfig()) {
                return hook.catharsis$getMetadata() != null && PackConfigHandler.isLoaded(hook.catharsis$getMetadata().getId());
            }
        }
        return true;
    }
}
