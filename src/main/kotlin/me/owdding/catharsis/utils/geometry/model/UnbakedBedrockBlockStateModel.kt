package me.owdding.catharsis.utils.geometry.model

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter
import net.fabricmc.fabric.api.renderer.v1.mesh.ShadeMode
import net.fabricmc.fabric.api.util.TriState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockModelPart
import net.minecraft.client.renderer.block.model.BlockStateModel
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.Material
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import java.util.function.Predicate

@GenerateCodec
data class UnbakedBedrockBlockStateModel(
    val model: ResourceLocation,
    val texture: ResourceLocation,
    val particle: ResourceLocation,
    @FieldName("ambient_occlusion") val ambientOcclusion: Boolean?
) : CustomUnbakedBlockStateModel {
    override fun codec(): MapCodec<out CustomUnbakedBlockStateModel> = CatharsisCodecs.getMapCodec<UnbakedBedrockBlockStateModel>()

    override fun bake(baker: ModelBaker): BlockStateModel {
        val texture = baker.sprites().get(Material(TextureAtlas.LOCATION_BLOCKS, texture)) { "Catharsis geo model $model/$texture" }
        val particle = baker.sprites().get(Material(TextureAtlas.LOCATION_BLOCKS, particle)) { "Catharsis geo model $model/$particle" }
        val baked = BedrockModelGeometryBaker.bakeBones(model, texture, ambientOcclusion).bones

        return BakedBedrockBlockStateModel(baked, particle, ambientOcclusion)
    }

    override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
    }
}

data class BakedBedrockModelBonePart(
    val quads: List<CulledQuad>,
    val children: List<BakedBedrockModelBonePart>,
    val ambientOcclusion: Boolean?
) : BlockModelPart {
    val allQuads: List<CulledQuad> = buildList {
        addAll(quads)
        children.forEach { addAll(it.allQuads) }
    }

    override fun emitQuads(emitter: QuadEmitter, cullTest: Predicate<Direction?>) {
        for (quad in allQuads) {
            val (baked, occlusionCulling) = quad
            if (occlusionCulling && cullTest.test(baked.direction)) continue

            if (occlusionCulling) emitter.cullFace(baked.direction)
            emitter.ambientOcclusion(TriState.of(ambientOcclusion))
            emitter.fromBakedQuad(baked)
            emitter.shadeMode(ShadeMode.VANILLA)
            emitter.emit()
        }
    }

    override fun getQuads(direction: Direction?): List<BakedQuad> = emptyList()

    override fun useAmbientOcclusion(): Boolean = ambientOcclusion == true

    override fun particleIcon(): TextureAtlasSprite? = null
}

data class BakedBedrockBlockStateModel(
    val bones: List<BakedBedrockModelBonePart>,
    val particle: TextureAtlasSprite,
    val occlusionCulling: Boolean?,
) : BlockStateModel {
    override fun collectParts(random: RandomSource, output: MutableList<BlockModelPart>) {
        output.addAll(bones)
    }

    override fun particleIcon(): TextureAtlasSprite = particle
}
