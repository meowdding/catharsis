package me.owdding.katharsis.mixins.pack;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.owdding.katharsis.Katharsis;
import me.owdding.katharsis.features.pack.config.PackConfigHandler;
import me.owdding.katharsis.features.pack.config.PackConfigOption;
import me.owdding.katharsis.features.pack.meta.KatharsisMetadataSection;
import me.owdding.katharsis.hooks.pack.PackMetadataHook;
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
    private static Pack.Metadata readKatharsisMetadata(PackLocationInfo info, Pack.ResourcesSupplier resources, PackFormat format, PackType type, Operation<Pack.Metadata> original) {
        // This needs to be done before the original method is called so that its done before fabric's so it can be used within the overlays
        var katharsisMetadata = type == PackType.CLIENT_RESOURCES ? katharsis$parseMetadata(resources, info) : null;
        var katharsisConfig = katharsis$parseConfig(resources, info);

        if (katharsisMetadata != null) {
            var config = katharsisConfig != null ? katharsisConfig : katharsisMetadata.getConfig();
            PackConfigHandler.updateDefaults(katharsisMetadata.getId(), config);
        }

        var metadata = original.call(info, resources, format, type);

        if (metadata != null) {
            metadata.katharsis$setMetadata(katharsisMetadata);
            metadata.katharsis$setConfig(katharsisConfig);

            if (katharsisConfig != null && katharsisMetadata != null && !katharsisMetadata.getConfig().isEmpty()) {
                Katharsis.INSTANCE.warn("Pack %s has both a katharsis metadata config section and a config.katharsis.json file, only config.katharsis.json will be used".formatted(info.id()));
            }
        }

        return metadata;
    }

    @Override
    public void katharsis$setMetadata(KatharsisMetadataSection metadata) {
        this.metadata.katharsis$setMetadata(metadata);
    }

    @Override
    public KatharsisMetadataSection katharsis$getMetadata() {
        return this.metadata.katharsis$getMetadata();
    }

    @Override
    public List<PackConfigOption> katharsis$getConfig() {
        return this.metadata.katharsis$getConfig();
    }

    @Override
    public boolean katharsis$requiresPackToOpenConfig() {
        return this.metadata.katharsis$requiresPackToOpenConfig();
    }

    @Unique
    private static KatharsisMetadataSection katharsis$parseMetadata(Pack.ResourcesSupplier resources, PackLocationInfo info) {
        try (var sources = resources.openPrimary(info)) {
            return sources.getMetadataSection(KatharsisMetadataSection.TYPE);
        } catch (Exception ignored) {
        }
        return null;
    }

    @Unique
    private static @Nullable List<PackConfigOption> katharsis$parseConfig(Pack.ResourcesSupplier resources, PackLocationInfo info) {
        try (var sources = resources.openPrimary(info)) {
            return PackConfigOption.fromResource(sources);
        } catch (Exception ignored) {
        }
        return null;
    }

    @ModifyReturnValue(method = "getTitle", at = @At("RETURN"))
    private Component katharsis$modifyTitle(Component originalTitle) {
        KatharsisMetadataSection meta = this.katharsis$getMetadata();
        if (meta != null && meta.getSelectedTitle() != null && this.katharsis$isLoaded()) {
            return meta.getSelectedTitle();
        }
        return originalTitle;
    }

    @ModifyReturnValue(method = "getDescription", at = @At("RETURN"))
    private Component katharsis$modifyDescription(Component originalDescription) {
        KatharsisMetadataSection meta = this.katharsis$getMetadata();
        if (meta != null && meta.getSelectedDescription() != null && this.katharsis$isLoaded()) {
            return meta.getSelectedDescription();
        }
        return originalDescription;
    }

    @Unique
    private boolean katharsis$isLoaded() {
        return McClient.INSTANCE.getSelf().getResourceManager().listPacks().anyMatch(p -> p.packId().equals(this.getId()));
    }

    /**
     * Taken from https://github.com/TheMysterys/Server-Pack-Unlocker under Unlicense License
     */
    @ModifyReturnValue(method = "isFixedPosition", at = @At("RETURN"))
    private boolean isFixedPosition(boolean original) {
        return false;
    }

    @Mixin(Pack.Metadata.class)
    public static class MetadataMixin implements PackMetadataHook {

        @Unique
        private KatharsisMetadataSection katharsis$metadata;
        @Unique
        private List<PackConfigOption> katharsis$config;

        @Override
        public void katharsis$setMetadata(KatharsisMetadataSection metadata) {
            this.katharsis$metadata = metadata;
        }

        @Override
        public KatharsisMetadataSection katharsis$getMetadata() {
            return this.katharsis$metadata;
        }

        @Override
        public void katharsis$setConfig(List<PackConfigOption> config) {
            this.katharsis$config = config;
        }

        @Override
        public List<PackConfigOption> katharsis$getConfig() {
            return Objects.requireNonNullElseGet(this.katharsis$config, () -> this.katharsis$metadata != null ? this.katharsis$metadata.getConfig() : List.of());
        }

        @Override
        public boolean katharsis$requiresPackToOpenConfig() {
            return this.katharsis$metadata != null && this.katharsis$metadata.getPackRequiredForConfig();
        }
    }
}
