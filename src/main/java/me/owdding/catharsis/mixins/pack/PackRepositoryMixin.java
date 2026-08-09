package me.owdding.catharsis.mixins.pack;

import me.owdding.catharsis.features.pack.PackOrderRetainer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin {

    @Shadow
    private List<Pack> selected;

    @Shadow
    @Nullable
    public abstract Pack getPack(String id);

    @Inject(method = {"setSelected", "reload"}, at = @At("TAIL"))
    private void catharsis$enforcePackOrder(CallbackInfo ci) {
        if (PackOrderRetainer.INSTANCE.isSaving()) return;

        List<String> currentIds = new ArrayList<>();
        for (Pack pack : this.selected) {
            currentIds.add(pack.getId());
        }

        Collection<String> sortedIds = PackOrderRetainer.INSTANCE.restoreOrder(currentIds, id -> {
            Pack pack = this.getPack(id);
            return pack != null && pack.getPackSource() == PackSource.SERVER;
        });

        List<Pack> newSelected = new ArrayList<>();
        for (String id : sortedIds) {
            Pack pack = this.getPack(id);
            if (pack != null) {
                newSelected.add(pack);
            }
        }

        this.selected = newSelected;
    }
}
