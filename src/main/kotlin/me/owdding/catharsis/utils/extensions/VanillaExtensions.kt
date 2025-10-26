package me.owdding.catharsis.utils.extensions

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.utils.extentions.plus
import kotlin.math.floor

operator fun <Key : Any, Value : Any> ExtraCodecs.LateBoundIdMapper<Key, Value>.set(key: Key, value: Value): ExtraCodecs.LateBoundIdMapper<Key, Value> = this.put(key, value)

fun BlockPos.offset(direction: Direction): BlockPos = BlockPos(this).plus(BlockPos(direction.unitVec3i))
fun Vec3.toBlockPos() = BlockPos(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
