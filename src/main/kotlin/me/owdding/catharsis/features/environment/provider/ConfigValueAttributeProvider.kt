package me.owdding.catharsis.features.environment.provider

import com.google.gson.JsonPrimitive
import me.owdding.catharsis.features.pack.config.PackConfigHandler
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.attribute.SpatialAttributeInterpolator
import net.minecraft.world.phys.Vec3

@GenerateCodec
data class ConfigValueAttributeProvider(
    val pack: String,
    val id: String,
) : EnvironmentalAttributeProvider<Int> {
    val color by lazy {
        PackConfigHandler.getConfig(pack).get(id)?.let {
            if (it is JsonPrimitive) {
                if (it.isNumber) return@let it.asNumber.toInt()
            }
            null
        }
    }

    override fun getValue(base: Int, pos: Vec3, biomeInterpolator: SpatialAttributeInterpolator?): Int? = color
}
