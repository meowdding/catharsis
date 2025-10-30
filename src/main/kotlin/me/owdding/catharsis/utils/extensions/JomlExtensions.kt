package me.owdding.catharsis.utils.extensions

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import org.joml.*

fun Vector2fc.copy(): Vector2fc = mutableCopy()
fun Vector2fc.mutableCopy(): Vector2f = Vector2f(this)
fun Vector2ic.copy(): Vector2ic = mutableCopy()
fun Vector2ic.mutableCopy(): Vector2i = Vector2i(this)
fun Vector2Lc.copy(): Vector2Lc = mutableCopy()
fun Vector2Lc.mutableCopy(): Vector2L = Vector2L(this)
fun Vector2dc.copy(): Vector2dc = mutableCopy()
fun Vector2dc.mutableCopy(): Vector2d = Vector2d(this)

fun Vector3fc.copy(): Vector3fc = mutableCopy()
fun Vector3fc.mutableCopy(): Vector3f = Vector3f(this)
fun Vector3ic.copy(): Vector3ic = mutableCopy()
fun Vector3ic.mutableCopy(): Vector3i = Vector3i(this)
fun Vector3Lc.copy(): Vector3Lc = mutableCopy()
fun Vector3Lc.mutableCopy(): Vector3L = Vector3L(this)
fun Vector3dc.copy(): Vector3dc = mutableCopy()
fun Vector3dc.mutableCopy(): Vector3d = Vector3d(this)

fun Vector4fc.copy(): Vector4fc = mutableCopy()
fun Vector4fc.mutableCopy(): Vector4f = Vector4f(this)
fun Vector4ic.copy(): Vector4ic = mutableCopy()
fun Vector4ic.mutableCopy(): Vector4i = Vector4i(this)
fun Vector4Lc.copy(): Vector4Lc = mutableCopy()
fun Vector4Lc.mutableCopy(): Vector4L = Vector4L(this)
fun Vector4dc.copy(): Vector4dc = mutableCopy()
fun Vector4dc.mutableCopy(): Vector4d = Vector4d(this)

fun BlockPos.toVector3ic(): Vector3ic = toVector3i()
fun BlockPos.toVector3i(): Vector3i = Vector3i(x, y, z)
fun Vector3ic.toBlockPos() = BlockPos(x(), y(), z())
fun Vector3ic.toVec3() = Vec3(toBlockPos())

fun Vec3.toVector3dc(): Vector3dc = toVector3d()
fun Vec3.toVector3d(): Vector3d = Vector3d(x, y, z)
fun Vector3dc.toVec3() = Vec3(x(), y(), z())
fun Vector3fc.toVec3() = Vec3(x().toDouble(), y().toDouble(), z().toDouble())
