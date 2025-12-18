package me.owdding.catharsis.mixins.pack;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.platform.InputConstants;
import kotlin.Pair;
import me.owdding.catharsis.features.pack.config.PackConfigScreen;
import me.owdding.catharsis.features.pack.meta.CatharsisMetadataSection;
import me.owdding.catharsis.hooks.pack.PackEntryHook;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
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
    @Shadow private static MultiLineLabel cacheDescription(Minecraft minecraft, Component text) {
        return null;
    }
    @Shadow private static FormattedCharSequence cacheName(Minecraft minecraft, Component name) {
        return null;
    }
    @Shadow @Final private TransferableSelectionList parent;

    @Unique private MultiLineLabel catharsis$incompatibleModDescriptionDisplayCache;
    @Unique private FormattedCharSequence catharsis$incompatibleModNameDisplayCache;

    @Unique private int right = 0;
    @Unique private int top = 0;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci, @Local(argsOnly = true) Minecraft minecraft) {
        this.catharsis$incompatibleModNameDisplayCache = cacheName(minecraft, Component.translatable("pack.catharsis.incompatible.title"));
        this.catharsis$incompatibleModDescriptionDisplayCache = cacheDescription(minecraft, Component.translatable("pack.catharsis.incompatible.desc"));
    }

    @Inject(
        method = "render",
        at = @At("HEAD")
    )
    private void renderConfigButton(
        CallbackInfo ci,
        @Local(argsOnly = true, ordinal = 1) int top,
        @Local(argsOnly = true, ordinal = 2) int left,
        @Local(argsOnly = true, ordinal = 3) int width
    ) {
        this.right = left + width - 3 - (this.parent.maxScrollAmount() > 0 ? 7 : 0);
        this.top = top;
    }

    @Definition(id = "incompatibleDescriptionDisplayCache", field = "Lnet/minecraft/client/gui/screens/packs/TransferableSelectionList$PackEntry;incompatibleDescriptionDisplayCache:Lnet/minecraft/client/gui/components/MultiLineLabel;")
    @Expression("? = ?.incompatibleDescriptionDisplayCache")
    @Inject(
        method = "render",
        at = @At(
            value = "MIXINEXTRAS:EXPRESSION",
            shift = At.Shift.AFTER
        )
    )
    private void renderDescription(
        CallbackInfo ci,
        @Local(ordinal = 0, argsOnly = true) GuiGraphics graphics,
        @Local(ordinal = 0, argsOnly = true) int mouseX,
        @Local(ordinal = 1, argsOnly = true) int mouseY,
        @Local(ordinal = 0) LocalRef<FormattedCharSequence> nameDisplayCache,
        @Local(ordinal = 0) LocalRef<MultiLineLabel> descriptionDisplayCache
    ) {
        var meta = catharsis$getMeta();
        List<Pair<String, ModContainer>> incompatibilities = meta != null ? meta.getIncompatibilities() : List.of();
        if (!incompatibilities.isEmpty()) {
            nameDisplayCache.set(this.catharsis$incompatibleModNameDisplayCache);
            descriptionDisplayCache.set(this.catharsis$incompatibleModDescriptionDisplayCache);
            graphics.setTooltipForNextFrame(this.minecraft.font, meta.getIncompatibleTooltip(), Optional.empty(), mouseX, mouseY);
        }
    }

    @Inject(
        method = "render",
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
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        var meta = catharsis$getMeta();
        if (meta == null) return;
        if (mouseX < this.right - 11 || mouseX > this.right) return;
        if (mouseY < this.top || mouseY > this.top + 11) return;
        if (button == InputConstants.MOUSE_BUTTON_LEFT && !meta.getConfig().isEmpty()) {
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
