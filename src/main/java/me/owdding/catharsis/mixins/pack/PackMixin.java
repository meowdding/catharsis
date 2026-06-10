package me.owdding.catharsis.mixins.pack;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.owdding.catharsis.Catharsis;
import me.owdding.catharsis.features.pack.config.PackConfigHandler;
import me.owdding.catharsis.features.pack.config.PackConfigOption;
import me.owdding.catharsis.features.pack.meta.CatharsisMetadataSection;
import me.owdding.catharsis.hooks.pack.PackMetadataHook;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.repository.Pack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.helpers.McClient;

import java.util.List;
import java.util.Objects;

@Mixin(Pack.class)
public abstract class PackMixin implements PackMetadataHook {

    @Shadow
    @Final
    private Pack.Metadata metadata;

    @Shadow
    public abstract String getId();

    @WrapOperation(
        method = "readMetaAndCreate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/packs/repository/Pack;readPackMetadata(Lnet/minecraft/server/packs/PackLocationInfo;Lnet/minecraft/server/packs/repository/Pack$ResourcesSupplier;Lnet/minecraft/server/packs/metadata/pack/PackFormat;Lnet/minecraft/server/packs/PackType;)Lnet/minecraft/server/packs/repository/Pack$Metadata;"
        )
    )
    private static Pack.Metadata readCatharsisMetadata(PackLocationInfo info, Pack.ResourcesSupplier resources, PackFormat format, PackType type, Operation<Pack.Metadata> original) {
        // This needs to be done before the original method is called so that its done before fabric's so it can be used within the overlays
        var catharsisMetadata = type == PackType.CLIENT_RESOURCES ? catharsis$parseMetadata(resources, info) : null;
        var catharsisConfig = catharsis$parseConfig(resources, info);

        if (catharsisMetadata != null) {
            var config = catharsisConfig != null ? catharsisConfig : catharsisMetadata.getConfig();
            PackConfigHandler.updateDefaults(catharsisMetadata.getId(), config);
        }

        var metadata = original.call(info, resources, format, type);

        if (metadata != null) {
            metadata.catharsis$setMetadata(catharsisMetadata);
            metadata.catharsis$setConfig(catharsisConfig);

            if (catharsisConfig != null && catharsisMetadata != null && !catharsisMetadata.getConfig().isEmpty()) {
                Catharsis.INSTANCE.warn("Pack %s has both a catharsis metadata config section and a config.catharsis.json file, only config.catharsis.json will be used".formatted(info.id()));
            }
        }

        return metadata;
    }

    @Override
    public void catharsis$setMetadata(CatharsisMetadataSection metadata) {
        this.metadata.catharsis$setMetadata(metadata);
    }

    @Override
    public CatharsisMetadataSection catharsis$getMetadata() {
        return this.metadata.catharsis$getMetadata();
    }

    @Override
    public List<PackConfigOption> catharsis$getConfig() {
        return this.metadata.catharsis$getConfig();
    }

    @Override
    public boolean catharsis$requiresPackToOpenConfig() {
        return this.metadata.catharsis$requiresPackToOpenConfig();
    }

    @Unique
    private static CatharsisMetadataSection catharsis$parseMetadata(Pack.ResourcesSupplier resources, PackLocationInfo info) {
        try (var sources = resources.openPrimary(info)) {
            return sources.getMetadataSection(CatharsisMetadataSection.TYPE);
        } catch (Exception ignored) {
        }
        return null;
    }

    @Unique
    private static @Nullable List<PackConfigOption> catharsis$parseConfig(Pack.ResourcesSupplier resources, PackLocationInfo info) {
        try (var sources = resources.openPrimary(info)) {
            return PackConfigOption.fromResource(sources);
        } catch (Exception ignored) {
        }
        return null;
    }

    @ModifyReturnValue(method = "getTitle", at = @At("RETURN"))
    private Component catharsis$modifyTitle(Component originalTitle) {
        CatharsisMetadataSection meta = this.catharsis$getMetadata();
        if (meta != null && meta.getSelectedTitle() != null && this.catharsis$isSelected()) {
            return meta.getSelectedTitle();
        }
        return originalTitle;
    }

    @ModifyReturnValue(method = "getDescription", at = @At("RETURN"))
    private Component catharsis$modifyDescription(Component originalDescription) {
        CatharsisMetadataSection meta = this.catharsis$getMetadata();
        if (meta != null && meta.getSelectedDescription() != null && this.catharsis$isSelected()) {
            return meta.getSelectedDescription();
        }
        return originalDescription;
    }

    @Unique
    private boolean catharsis$isSelected() {
        return McClient.INSTANCE.getSelf().getResourcePackRepository().getSelectedIds().contains(this.getId());
    }

    @Mixin(Pack.Metadata.class)
    public static class MetadataMixin implements PackMetadataHook {

        @Unique
        private CatharsisMetadataSection catharsis$metadata;
        @Unique
        private List<PackConfigOption> catharsis$config;

        @Override
        public void catharsis$setMetadata(CatharsisMetadataSection metadata) {
            this.catharsis$metadata = metadata;
        }

        @Override
        public CatharsisMetadataSection catharsis$getMetadata() {
            return this.catharsis$metadata;
        }

        @Override
        public void catharsis$setConfig(List<PackConfigOption> config) {
            this.catharsis$config = config;
        }

        @Override
        public List<PackConfigOption> catharsis$getConfig() {
            return Objects.requireNonNullElseGet(this.catharsis$config, () -> this.catharsis$metadata != null ? this.catharsis$metadata.getConfig() : List.of());
        }

        @Override
        public boolean catharsis$requiresPackToOpenConfig() {
            return this.catharsis$metadata != null && this.catharsis$metadata.getPackRequiredForConfig();
        }
    }
}
