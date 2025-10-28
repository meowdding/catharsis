package me.owdding.catharsis.features.misc

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.TextureFormat
import me.owdding.catharsis.utils.CatharsisLogger
import net.minecraft.client.renderer.texture.*
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection
import net.minecraft.client.resources.metadata.animation.FrameSize
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.Mth
import java.io.IOException
import java.util.*
import kotlin.jvm.optionals.getOrNull

class AnimatableSimpleTexture(location: ResourceLocation) : SimpleTexture(location), Tickable {

    private var contents: SpriteContents? = null
    private var ticker: SpriteTicker? = null

    val canTick: Boolean get() = contents?.isAnimated == true

    @Throws(IOException::class)
    override fun loadContents(manager: ResourceManager): TextureContents {
        val id = resourceId()
        val resource = manager.getResourceOrThrow(id)
        val image: NativeImage = resource.open().use(NativeImage::read)
        val textureMetadata = resource.metadata().getSection(TextureMetadataSection.TYPE).getOrNull()
        val animatedMetadata = resource.metadata().getSection(AnimationMetadataSection.TYPE).getOrNull()
        val frameSize = animatedMetadata?.calculateFrameSize(image.width, image.height) ?: FrameSize(image.width, image.height)

        this.contents?.close()
        this.ticker?.close()

        this.ticker = null

        if (!Mth.isMultipleOf(image.width, frameSize.width) || !Mth.isMultipleOf(image.height, frameSize.height)) {
            logger.error("Image $id size ${image.width}x${image.height} is not a multiple of frame size ${frameSize.width}x${frameSize.height}")
            this.contents = SpriteContents(id, FrameSize(image.width, image.height), image)
        } else {
            this.contents = SpriteContents(id, frameSize, image, Optional.ofNullable(animatedMetadata), listOf())
        }

        return TextureContents(image, textureMetadata)
    }

    override fun apply(contents: TextureContents) {
        // We don't call the super as that try resources the image
        // which we need to have still loaded to read frames from
        doLoad(contents.image, contents.blur(), contents.clamp())
    }

    public override fun doLoad(image: NativeImage, blur: Boolean, clamp: Boolean) {
        val device = RenderSystem.getDevice()
        val id = resourceId()

        this.texture?.close()
        this.textureView?.close()
        this.texture = device.createTexture(id::toString, 5, TextureFormat.RGBA8, this.contents?.width() ?: 0, this.contents?.height() ?: 0, 1, 1)
        this.textureView = device.createTextureView(this.texture!!)

        this.setFilter(blur, false)
        this.setClamp(clamp)
        this.contents?.uploadFirstFrame(0, 0, this.texture!!)

        this.ticker = this.contents?.createTicker()

        if (this.ticker == null) {
            image.close() // Close the image if we don't need it anymore for animations
        }
    }

    override fun tick() {
        val texture = this.texture ?: return
        val ticker = this.ticker ?: return
        ticker.tickAndUpload(0, 0, texture)
    }

    override fun close() {
        super.close()
        this.contents?.close()
        this.ticker?.close()

        this.ticker = null
        this.contents = null
    }

    companion object {
        private val logger = CatharsisLogger.named("AnimatedTextures")
    }
}
