package me.owdding.catharsis.features.entity.models

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.TypedResourceManager
import me.owdding.catharsis.utils.extensions.unsafeCast
import me.owdding.catharsis.utils.geometry.BedrockGeometry
import me.owdding.catharsis.utils.geometry.SafeModelPart
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.player.PlayerModel
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.Entity

class CustomEntityModel(val variants: List<Variant>) {

    fun getVariant(entity: Entity): Variant {
        if (variants.isEmpty()) error("CustomEntityModel has no variants!")
        if (variants.size == 1) return variants.first()

        val totalWeight = variants.sumOf { it.weight }
        if (totalWeight <= 0) return variants.first()

        var value = entity.uuid.hashCode() % totalWeight
        for (variant in variants) {
            value -= variant.weight
            if (value < 0) return variant
        }
        return variants.first()
    }

    data class Variant(
        val texture: Identifier,
        val emissiveTexture: Identifier?,
        val model: ModelPart?,
        val isTranslucent: Boolean = false,
        val weight: Int = 1,
    ) {

        private var cachedEntityModel: EntityModel<out EntityRenderState>? = null

        fun <T : EntityRenderState> replaceModel(oldModel: EntityModel<T>): EntityModel<T> {
            val newCustomEntityModelPart = model ?: return oldModel

            val cachedEntityModel = cachedEntityModel
            if (cachedEntityModel != null) {
                return cachedEntityModel.unsafeCast()
            }

            if (oldModel is PlayerModel) {
                val newPlayerModel = PlayerModel(newCustomEntityModelPart, oldModel.slim)

                this.cachedEntityModel = newPlayerModel
                return newPlayerModel.unsafeCast()
            }

            val newModel = runCatching {
                oldModel.javaClass.getConstructor(ModelPart::class.java).newInstance(newCustomEntityModelPart)
            }.getOrElse {
                Catharsis.error("Failed to replace a model: Failed to construct ${oldModel.javaClass.name}", it)

                // If the constructor doesn't exist on one call it is unlikely to exist in the future
                oldModel
            }

            this.cachedEntityModel = newModel

            return newModel
        }
    }

    @GenerateCodec
    @NamedCodec("UnbakedCustomEntityModel")
    data class Unbaked(
        val variants: List<UnbakedVariant>?,
        val texture: Identifier?,
        @FieldName("emissive_texture") val emissiveTexture: Identifier?,
        val model: Identifier?,
        val translucent: Boolean = false,
        val weight: Int = 1,
    ) {
        fun bake(resources: TypedResourceManager): CustomEntityModel {
            val bakedVariants = if (!variants.isNullOrEmpty()) {
                variants.map { it.bake(resources) }
            } else {
                requireNotNull(texture) { "Entity model must either define 'variants' or a 'texture'" }
                listOf(UnbakedVariant(texture, emissiveTexture, model, translucent, weight).bake(resources))
            }

            return CustomEntityModel(bakedVariants)
        }
    }

    @GenerateCodec
    @NamedCodec("UnbakedCustomEntityModelVariant")
    data class UnbakedVariant(
        val texture: Identifier,
        @FieldName("emissive_texture") val emissiveTexture: Identifier?,
        val model: Identifier?,
        val translucent: Boolean = false,
        val weight: Int = 1,
    ) {
        fun bake(resources: TypedResourceManager): Variant {
            val bakedModel = if (model != null) {
                val bedrockModel = resources.getOrLoad(model, BedrockGeometry.RESOURCE_PARSER)!!.getOrThrow()

                SafeModelPart.convertFromBedrockModel(bedrockModel)
            } else null

            return Variant(
                texture,
                emissiveTexture,
                bakedModel,
                translucent,
                weight,
            )
        }
    }
}
