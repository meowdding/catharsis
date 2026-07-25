package me.owdding.katharsis.mixins.pack;

import me.owdding.katharsis.features.pack.PackOrderRetainer;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PackSelectionModel.class)
public class PackSelectionModelMixin {

    @Final
    @Shadow
    private PackRepository repository;

    @Inject(method = "commit", at = @At("HEAD"))
    private void katharsis$beginPackSave(CallbackInfo ci) {
        PackOrderRetainer.INSTANCE.setSaving(true);
    }

    @Inject(method = "commit", at = @At("TAIL"))
    private void katharsis$endPackSave(CallbackInfo ci) {
        PackOrderRetainer.INSTANCE.setSaving(false);
        PackOrderRetainer.INSTANCE.saveCurrentOrder(this.repository.getSelectedIds(), id -> {
            Pack pack = this.repository.getPack(id);
            return pack != null && pack.getPackSource() == PackSource.SERVER;
        });
    }
}
