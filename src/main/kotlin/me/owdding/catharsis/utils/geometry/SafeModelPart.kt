package me.owdding.catharsis.utils.geometry

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.extensions.toVector3f
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.model.geom.builders.PartDefinition
import org.joml.Vector3f
import org.joml.plus
import java.util.function.Function
import kotlin.jvm.optionals.getOrNull

class SafeModelPart(
    cubes: List<Cube>,
    children: Map<String, ModelPart>,
    initialPose: PartPose,
    val name: String = "Unnamed",
    val path: String = "root",
) : ModelPart(cubes, children) {

    init {
        this.initialPose = initialPose
        this.loadPose(initialPose)
    }

    override fun getChild(name: String): ModelPart {
        return if (super.hasChild(name)) {
            val unsafeModelPart = super.getChild(name)

            SafeModelPart(
                unsafeModelPart.cubes,
                unsafeModelPart.children,
                unsafeModelPart.initialPose,
                this.name,
                "$path/$name",
            )
        } else {
            Catharsis.warn("model ${this.name} missing bone $name!")
            DummyModelPart()
        }
    }

    override fun createPartLookup(): Function<String, ModelPart?> {
        val parentLookup = super.createPartLookup()
        return Function { boneName: String ->
            val parentBone = parentLookup.apply(boneName)
            if (parentBone == null) {
                Catharsis.warn("Bone $boneName in model $name is missing!")
                DummyModelPart()
            } else parentBone
        }
    }

    companion object {
        private fun CubeListBuilder.addBedrockCube(pivot: Vector3f, from: Vector3f, size: Vector3f, uv: List<Float>, inflate: Float?, mirror: Boolean?) {
            val to = from + size

            val cubeOrigin = Vector3f(
                -(pivot.x - to.x),
                -from.y - size.y + pivot.y,
                from.z - pivot.z,
            )

            texOffs(uv[0].toInt(), uv[1].toInt())
            if (mirror == true) mirror()
            addBox(
                cubeOrigin.x, cubeOrigin.y, cubeOrigin.z,
                size.x, size.y, size.z,
                CubeDeformation(inflate ?: 0f),
            )
            if (mirror == true) mirror(false)
        }

        fun convertFromBedrockModel(model: BedrockGeometry?): ModelPart? {
            if (model == null) return null

            val meshDefinition = MeshDefinition()
            val root = meshDefinition.root
            val knownBones = mutableMapOf<String?, PartDefinition>()
            val knownOffsets = mutableMapOf<String?, Vector3f>()

            knownBones[null] = root
            knownOffsets[null] = Vector3f(0f, 24f, 0f)

            for (bone in model.bones) {
                val cubesBuilder = CubeListBuilder.create()

                val parentBone = knownBones[bone.parent] ?: continue
                val offsetOffset = knownOffsets[bone.parent] ?: continue
                val pivot = bone.pivot.toVector3f()
                val offset = Vector3f(
                    (pivot.x - offsetOffset.x),
                    -(pivot.y - offsetOffset.y),
                    pivot.z - offsetOffset.z,
                )

                val rotatedCubes = mutableListOf<BedrockCube>()

                for (cube in bone.cubes) {
                    if (cube.uv?.right()?.isPresent ?: true) continue
                    val uv = cube.uv.left().getOrNull() ?: continue
                    if (cube.rotation.any { it != 0f }) {
                        rotatedCubes.add(cube)
                        continue
                    }

                    val size = cube.size.toVector3f()
                    val origin = cube.origin.toVector3f()
                    val from = Vector3f(
                        origin.x - size.x,
                        origin.y,
                        origin.z
                    )

                    cubesBuilder.addBedrockCube(pivot, from, size, uv, cube.inflate, cube.mirror)
                }

                knownOffsets[bone.name] = pivot

                val child = parentBone.addOrReplaceChild(
                    bone.name,
                    cubesBuilder,
                    PartPose.offsetAndRotation(
                        offset.x, offset.y, offset.z,
                        Math.toRadians(bone.rotation[0].toDouble()).toFloat(),
                        Math.toRadians(bone.rotation[1].toDouble()).toFloat(),
                        Math.toRadians(bone.rotation[2].toDouble()).toFloat()
                    )
                )

                for (cube in rotatedCubes) {
                    val subCubeBuilder = CubeListBuilder.create()

                    val uv = cube.uv?.left()?.getOrNull() ?: continue
                    val size = cube.size.toVector3f()
                    val origin = cube.origin.toVector3f()
                    val from = Vector3f(
                        origin.x - size.x,
                        origin.y,
                        origin.z
                    )
                    val cubePivot = cube.pivot.toVector3f()

                    subCubeBuilder.addBedrockCube(cubePivot, from, size, uv, cube.inflate, cube.mirror)

                    val cubePivotOffset = Vector3f(
                        pivot.x - cubePivot.x,
                        pivot.y - cubePivot.y,
                        cubePivot.z - pivot.z,
                    )

                    child.addOrReplaceChild("rotate_bone", subCubeBuilder, PartPose.offsetAndRotation(
                        cubePivotOffset.x, cubePivotOffset.y, cubePivotOffset.z,
                        Math.toRadians(cube.rotation[0].toDouble()).toFloat(),
                        Math.toRadians(cube.rotation[1].toDouble()).toFloat(),
                        Math.toRadians(cube.rotation[2].toDouble()).toFloat()
                    ))
                }

                knownBones[bone.name] = child
            }

            val unsafeModelPart = root.bake(model.description.textureWidth, model.description.textureHeight)

            return SafeModelPart(
                unsafeModelPart.cubes,
                unsafeModelPart.children,
                unsafeModelPart.initialPose,
                name = model.description.identifier,
            )
        }
    }
}

class DummyModelPart : ModelPart(mutableListOf(), mutableMapOf()) {
    override fun getChild(name: String): ModelPart {
        return children.getOrPut(name) {
            DummyModelPart()
        }
    }

    override fun createPartLookup(): Function<String, ModelPart?> {
        return Function {
            DummyModelPart()
        }
    }
}
