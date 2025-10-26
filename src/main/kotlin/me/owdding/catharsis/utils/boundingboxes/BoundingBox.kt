package me.owdding.catharsis.utils.boundingboxes

import me.owdding.catharsis.utils.extensions.mutableCopy
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.AABB
import org.joml.Vector3i
import org.joml.Vector3ic
import kotlin.math.max
import kotlin.math.min
import net.minecraft.world.level.levelgen.structure.BoundingBox as MinecraftBox

@GenerateCodec
data class BoundingBox(
    val min: Vector3i,
    val max: Vector3i,
) {

    companion object {
        fun encapsulatingBoxes(iterable: Iterable<BoundingBox>) = encapsulatingVectors(iterable.flatMap { listOf(it.max, it.min) })
        fun encapsulatingVectors(iterable: Iterable<Vector3ic>): BoundingBox? {
            val iterator = iterable.iterator()
            if (iterator.hasNext()) {
                val min = iterator.next().mutableCopy()
                val max = min.mutableCopy()

                iterator.forEach {
                    min.x = min(min.x, it.x())
                    min.y = min(min.y, it.y())
                    min.z = min(min.z, it.z())
                    max.x = max(max.x, it.x())
                    max.y = max(max.y, it.y())
                    max.z = max(max.z, it.z())
                }

                return BoundingBox(min, max)
            } else {
                return null
            }
        }
    }

    constructor(pos: Vector3ic) : this(pos.mutableCopy(), pos.mutableCopy().add(1, 1, 1))

    constructor(
        minX: Int,
        minY: Int,
        minZ: Int,
        maxX: Int,
        maxY: Int,
        maxZ: Int,
    ) : this(
        Vector3i(
            min(minX, maxX),
            min(minY, maxY),
            min(minZ, maxZ),
        ),
        Vector3i(
            max(minX, maxX),
            max(minY, maxY),
            max(minZ, maxZ),
        ),
    )

    init {
        if (min.x > max.x || min.y > max.y || min.z > max.z) {
            fun getMinMax(component: Vector3i.() -> Int): Pair<Int, Int> {
                return if (min.component() > max.component()) max.component() to min.component()
                else min.component() to max.component()
            }
            val (minX, maxX) = getMinMax { x }
            val (minY, maxY) = getMinMax { y }
            val (minZ, maxZ) = getMinMax { z }
            min.x = minX
            min.y = minY
            min.z = minZ
            max.x = maxX
            max.y = maxY
            max.z = maxZ
        }
    }

    val xSpan get() = max.x - min.x + 1
    val ySpan get() = max.y - min.y + 1
    val zSpan get() = max.z - min.z + 1
    val center: Vector3i get() = min.mutableCopy().add(max.mutableCopy().sub(min).div(2).add(1, 1, 1))

    operator fun contains(pos: Vector3i) = (pos.x <= min.x && pos.x >= max.x) && (pos.y <= min.y && pos.y >= max.y) && (pos.z <= min.z && pos.z >= max.z)
    operator fun contains(pos: Vec3i) = (pos.x <= min.x && pos.x >= max.x) && (pos.y <= min.y && pos.y >= max.y) && (pos.z <= min.z && pos.z >= max.z)

    fun intersects(other: BoundingBox) =
        max.x >= other.min.x && min.x <= other.max.x && max.z >= other.min.z && min.z <= other.max.z && max.y >= other.min.y && min.y <= other.max.y

    fun inflateBy(amount: Int) = inflateBy(Vector3i(amount))
    fun inflateBy(vec: Vector3ic) = BoundingBox(min.mutableCopy().sub(vec), max.mutableCopy().add(vec))

    fun toMinecraftBox() = MinecraftBox(min.x, min.y, min.z, max.x, max.y, max.z)
    fun toMinecraftAABB() = AABB.of(toMinecraftBox())
}
