package me.owdding.catharsis.utils.boundingboxes

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.utils.codecs.PosCodecs
import me.owdding.catharsis.utils.extensions.mutableCopy
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.AABB
import org.joml.Vector3i
import org.joml.Vector3ic
import kotlin.math.max
import kotlin.math.min
import net.minecraft.world.level.levelgen.structure.BoundingBox as MinecraftBox


data class BoundingBox(
    val min: Vector3i,
    val max: Vector3i,
) {

    constructor(min: Vector3ic, max: Vector3ic) : this(min.mutableCopy(), max.mutableCopy())

    companion object {
        @IncludedCodec
        val CODEC: Codec<BoundingBox> = Codec.either(
            RecordCodecBuilder.create {
                it.group(
                    PosCodecs.vector3icCodec.fieldOf("min").forGetter(BoundingBox::min),
                    PosCodecs.vector3icCodec.fieldOf("max").forGetter(BoundingBox::max),
                ).apply(it, ::BoundingBox)
            },
            Codec.either(
                PosCodecs.vector3icCodec.listOf(2, 2).xmap({ BoundingBox(it[0], it[1]) }, { listOf(it.min, it.max) }),
                Codec.INT.listOf(6, 6).xmap({ BoundingBox(it[0], it[1], it[2], it[3], it[4], it[5]) }, { listOf(it.min.x, it.min.y, it.min.z, it.max.x, it.max.y, it.max.x) }),
            ).xmap(Either<BoundingBox, BoundingBox>::unwrap) { Either.left(it) },
        ).xmap(Either<BoundingBox, BoundingBox>::unwrap) { Either.left(it) }

        fun encapsulating(iterable: Iterable<BoundingBox>) = encapsulating(iterable.flatMap { listOf(it.max, it.min) })
        fun encapsulating(iterable: Iterable<Vector3ic>): BoundingBox? {
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
    fun toMinecraftAABB(): AABB = AABB.of(toMinecraftBox())
}
