package me.owdding.catharsis.utils.extensions

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import me.owdding.catharsis.utils.types.Base64String
import me.owdding.catharsis.utils.types.requireBase64Padding
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.packs.resources.Resource
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.platform.identifier
import tech.thatgravyboat.skyblockapi.utils.extentions.getTexture
import tech.thatgravyboat.skyblockapi.utils.extentions.plus
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import kotlin.math.floor

operator fun <Key : Any, Value : Any> ExtraCodecs.LateBoundIdMapper<Key, Value>.set(key: Key, value: Value): ExtraCodecs.LateBoundIdMapper<Key, Value> = this.put(key, value)

fun BlockPos.offset(direction: Direction): BlockPos {
    val unitVec = direction.unitVec3i
    val unitVecBlockPos = BlockPos(unitVec.x, unitVec.y, unitVec.z)
    return BlockPos(this.x, this.y, this.z).plus(unitVecBlockPos)
}

fun Vec3.toBlockPos() = BlockPos(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())

inline fun <reified T : Any> Resource.readAsJson(): T = this.open().use { it.readJson<T>() }
fun <T : Any> Resource.readWithCodec(codec: Codec<T>): T = this.open().use { it.readJson<JsonElement>().toDataOrThrow(codec) }

@Suppress("DEPRECATION")
val Block.identifier get() = this.builtInRegistryHolder().key().identifier

val ItemStack.base64Texture: Base64String? get() = this.getTexture()?.requireBase64Padding()
