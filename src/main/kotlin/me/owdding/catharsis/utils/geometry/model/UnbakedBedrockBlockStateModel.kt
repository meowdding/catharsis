package me.owdding.catharsis.utils.geometry.model

import com.mojang.serialization.MapCodec
import me.owdding.catharsis.generated.CatharsisCodecs
import me.owdding.ktcodecs.GenerateCodec
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel
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

@GenerateCodec
data class UnbakedBedrockBlockStateModel(
    val model: ResourceLocation,
    val texture: ResourceLocation,
    val particle: ResourceLocation?,
) : CustomUnbakedBlockStateModel {
    override fun codec(): MapCodec<out CustomUnbakedBlockStateModel> = CatharsisCodecs.getMapCodec<UnbakedBedrockBlockStateModel>()

    override fun bake(baker: ModelBaker): BlockStateModel {
        val texture = baker.sprites().get(Material(TextureAtlas.LOCATION_BLOCKS, texture)) { "Catharsis geo model $model/$texture" }
        val particle = particle?.let { baker.sprites().get(Material(TextureAtlas.LOCATION_BLOCKS, particle)) { "Catharsis geo model $model/$particle" } }
        val baked = BedrockModelGeometryBaker.bakeBones(model, texture).bones

        return BakedBedrockBlockStateModel(baked, particle)
    }

    override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
    }
}

data class BakedBedrockModelBonePart(
    val quads: List<BakedQuad>,
    val children: List<BakedBedrockModelBonePart>,
) : BlockModelPart {
    val allQuads: List<BakedQuad> = buildList {
        addAll(quads)
        children.forEach { addAll(it.allQuads) }
    }

    override fun getQuads(direction: Direction?): List<BakedQuad> = allQuads

    override fun useAmbientOcclusion(): Boolean = false

    override fun particleIcon(): TextureAtlasSprite? = null
}

data class BakedBedrockBlockStateModel(
    val bones: List<BakedBedrockModelBonePart>,
    val particle: TextureAtlasSprite?,
) : BlockStateModel {
    override fun collectParts(random: RandomSource, output: MutableList<BlockModelPart>) {
        output.addAll(bones)
    }

    override fun particleIcon(): TextureAtlasSprite? = particle
}
