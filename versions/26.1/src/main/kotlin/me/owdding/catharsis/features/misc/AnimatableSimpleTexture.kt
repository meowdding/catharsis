package me.owdding.catharsis.features.misc

import com.mojang.blaze3d.buffers.GpuBuffer
import com.mojang.blaze3d.buffers.Std140Builder
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderPass
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.AddressMode
import com.mojang.blaze3d.textures.FilterMode
import com.mojang.blaze3d.textures.GpuTexture
import com.mojang.blaze3d.textures.TextureFormat
import me.owdding.catharsis.utils.CatharsisLogger
import net.minecraft.client.renderer.texture.SimpleTexture
import net.minecraft.client.renderer.texture.SpriteContents
import net.minecraft.client.renderer.texture.TextureContents
import net.minecraft.client.renderer.texture.TickableTexture
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection
import net.minecraft.client.resources.metadata.animation.FrameSize
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.Mth
import org.joml.Matrix4f
import org.lwjgl.system.MemoryUtil
import java.io.Closeable
import java.io.IOException
import java.util.*
import kotlin.jvm.optionals.getOrNull

class AnimatableSimpleTexture(location: Identifier) : SimpleTexture(location), TickableTexture {

    private var contents: SpriteContents? = null
    private var data: AnimationData? = null

    val canTick: Boolean get() = contents?.isAnimated == true

    @Throws(IOException::class)
    override fun loadContents(manager: ResourceManager): TextureContents {
        this.contents?.close()
        this.contents = null

        val id = resourceId()
        val resource = manager.getResourceOrThrow(id)
        val image: NativeImage = resource.open().use(NativeImage::read)
        val textureMetadata = resource.metadata().getSection(TextureMetadataSection.TYPE).getOrNull()
        var animatedMetadata = resource.metadata().getSection(AnimationMetadataSection.TYPE).getOrNull()
        var frameSize = animatedMetadata?.calculateFrameSize(image.width, image.height) ?: FrameSize(image.width, image.height)

        if (!Mth.isMultipleOf(image.width, frameSize.width) || !Mth.isMultipleOf(image.height, frameSize.height)) {
            logger.error("Image $id size ${image.width}x${image.height} is not a multiple of frame size ${frameSize.width}x${frameSize.height}")
            frameSize = FrameSize(image.width, image.height)
            animatedMetadata = null
        }
        this.contents = SpriteContents(id, frameSize, image, Optional.ofNullable(animatedMetadata), listOf(), Optional.ofNullable(textureMetadata))

        return TextureContents(image, textureMetadata)
    }

    override fun apply(contents: TextureContents) {
        // We don't call the super as that try resources the image
        // which we need to have still loaded to read frames from
        val address = if (contents.clamp()) AddressMode.CLAMP_TO_EDGE else AddressMode.REPEAT
        val filter = if (contents.blur()) FilterMode.LINEAR else FilterMode.NEAREST
        this.sampler = RenderSystem.getSamplerCache().getSampler(address, address, filter, filter, false)

        doLoad(contents.image)
    }

    public override fun doLoad(image: NativeImage) {
        this.data?.close()
        this.data = null

        val device = RenderSystem.getDevice()
        val id = resourceId()

        this.texture?.close()
        this.textureView?.close()
        this.texture = device.createTexture(
            id::toString,
            GpuTexture.USAGE_COPY_DST or GpuTexture.USAGE_TEXTURE_BINDING or GpuTexture.USAGE_RENDER_ATTACHMENT,
            TextureFormat.RGBA8,
            this.contents?.width() ?: image.width, this.contents?.height() ?: image.height,
            1, 1
        )
        this.textureView = device.createTextureView(this.texture!!)

        this.contents?.uploadFirstFrame(this.texture!!, 0)
        this.data = this.contents?.createAndUploadState(id.toString())

        if (this.contents == null) {
            device.createCommandEncoder().writeToTexture(this.texture!!, image)
        }

        if (this.data == null) {
            image.close() // Close the image if we don't need it anymore for animations
        }
    }

    override fun tick() {
        val view = this.textureView ?: return
        val data = this.data ?: return

        if (data.tick()) {
            RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass({ "Animate " + this.resourceId() }, view, OptionalInt.empty())
                .use(data::drawToPass)
        }
    }

    override fun close() {
        super.close()
        this.contents?.close()
        this.data?.close()

        this.data = null
        this.contents = null
    }

    private data class AnimationData(val state: SpriteContents.AnimationState, val buffer: GpuBuffer) : Closeable {

        fun tick(): Boolean {
            state.tick()
            return state.needsToDraw()
        }

        fun drawToPass(pass: RenderPass) {
            state.drawToAtlas(pass, state.getDrawUbo(0))
        }

        override fun close() {
            state.close()
            buffer.close()
        }
    }

    companion object {
        private val logger = CatharsisLogger.named("AnimatedTextures")

        private fun SpriteContents.createAndUploadState(label: String): AnimationData? {
            if (!this.isAnimated) return null

            val size = Mth.roundToward(SpriteContents.UBO_SIZE, RenderSystem.getDevice().uniformOffsetAlignment)
            val byteBuffer = MemoryUtil.memAlloc(size)
            Std140Builder.intoBuffer(MemoryUtil.memSlice(byteBuffer, 0, size))
                .putMat4f(Matrix4f().ortho2D(0.0F, this.width().toFloat(), 0.0F,this.height().toFloat()))
                .putMat4f(Matrix4f().scale(this.width().toFloat(), this.height().toFloat(), 1.0F))
                .putFloat(0f)
                .putFloat(0f)
                .putInt(0)

            val gpuBuffer = RenderSystem.getDevice().createBuffer({ "$label sprite UBOs" }, GpuBuffer.USAGE_UNIFORM, byteBuffer)
            val state = this.createAnimationState(gpuBuffer.slice(0, size.toLong()), size)!!

            return AnimationData(state, gpuBuffer)
        }
    }
}
