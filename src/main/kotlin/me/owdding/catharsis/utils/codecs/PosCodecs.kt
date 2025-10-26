package me.owdding.catharsis.utils.codecs

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.catharsis.utils.extensions.mutableCopy
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector3i
import org.joml.Vector3ic
import java.util.function.Function

object PosCodecs {

    @IncludedCodec val vector3fCodec: Codec<Vector3f> = createCodec(
        ::Vector3f,
        Codec.FLOAT,
        String::toFloat,
        { x }, { y }, { z },
    )

    @IncludedCodec val vector3fcCodec: Codec<Vector3fc> = vector3fCodec.xmap({ it }, { it.mutableCopy() })

    @IncludedCodec val vector3dCodec: Codec<Vector3d> = createCodec(
        ::Vector3d,
        Codec.DOUBLE,
        String::toDouble,
        { x }, { y }, { z },
    )
    @IncludedCodec val vector3dcCodec: Codec<Vector3dc> = vector3dCodec.xmap({ it }, { it.mutableCopy() })

    @IncludedCodec val vector3iCodec: Codec<Vector3i> = createCodec(
        ::Vector3i,
        Codec.INT,
        String::toInt,
        { x }, { y }, { z },
    )
    @IncludedCodec val vector3icCodec: Codec<Vector3ic> = vector3iCodec.xmap({ it }, { it.mutableCopy() })

    @IncludedCodec val vec3Codec: Codec<Vec3> = vector3dCodec.xmap({ Vec3(it.x, it.y, it.z) }, { Vector3d(it.x, it.y, it.z) })
    @IncludedCodec val vec3iCodec: Codec<Vec3i> = vector3iCodec.xmap({ Vec3i(it.x, it.y, it.z) }, { Vector3i(it.x, it.y, it.z) })
    @IncludedCodec val blockPosCodec: Codec<BlockPos> = vec3iCodec.xmap(::BlockPos, Function.identity())

    val aabbCodec: Codec<AABB> = RecordCodecBuilder.create {
        it.group(
            vec3Codec.fieldOf("min").forGetter(AABB::getMinPosition),
            vec3Codec.fieldOf("max").forGetter(AABB::getMaxPosition)
        ).apply(it, ::AABB)
    }

    val boundingBox: Codec<BoundingBox> = RecordCodecBuilder.create {
        it.group(
            blockPosCodec.fieldOf("min").forGetter { BlockPos(it.minX(), it.minY(), it.minZ())},
            blockPosCodec.fieldOf("max").forGetter { BlockPos(it.maxX(), it.maxY(), it.maxZ())},
        ).apply(it, BoundingBox::fromCorners)
    }



    fun <VecType : Any, NumberType : Any> createCodec(
        constructor: (NumberType, NumberType, NumberType) -> VecType,
        numberCodec: Codec<NumberType>,
        toNumber: String.() -> NumberType,
        first: VecType.() -> NumberType,
        second: VecType.() -> NumberType,
        third: VecType.() -> NumberType,
    ): Codec<VecType> {
        return Codec.either(
            Codec.either(
                numberCodec.listOf().comapFlatMap({ list -> Util.fixedSize(list, 3).map { constructor(it[0], it[1], it[2]) } }, { listOf(it.first(), it.second(), it.third()) }),
                RecordCodecBuilder.create<VecType> {
                    it.group(
                        numberCodec.fieldOf("x").forGetter { vec -> vec.first() },
                        numberCodec.fieldOf("y").forGetter { vec -> vec.second() },
                        numberCodec.fieldOf("z").forGetter { vec -> vec.third() },
                    ).apply(it, constructor)
                },
            ).xmap(Either<VecType, VecType>::unwrap) { Either.left(it) },
            Codec.STRING.xmap(
                {
                    it.split(":").let { elements ->
                        constructor(elements[0].toNumber(), elements[1].toNumber(), elements[2].toNumber())
                    }
                },
                { "${it.first()}:${it.second()}:${it.third()}" },
            ),
        ).xmap(Either<Vector3f, Vector3f>::unwrap) { Either.left(it) }
    }

}
