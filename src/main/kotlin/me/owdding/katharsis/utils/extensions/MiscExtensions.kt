package me.owdding.katharsis.utils.extensions

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.serialization.MapCodec
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.client.gui.components.MultiLineTextWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.multiplayer.CacheSlot
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.gizmos.GizmoStyle
import net.minecraft.gizmos.Gizmos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.ARGB
import net.minecraft.util.RegistryContextSwapper
import net.minecraft.world.phys.AABB
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Suppress("UNCHECKED_CAST")
fun <To> Any?.unsafeCast(): To = this as To

val KClass<*>.isNumber: Boolean get() = java.isNumber
val Class<*>.isNumber: Boolean
    get() {
        if (Number::class.java.isAssignableFrom(this)) return true
        if (this == Int::class.javaPrimitiveType) return true
        if (this == Double::class.javaPrimitiveType) return true
        if (this == Float::class.javaPrimitiveType) return true
        if (this == Long::class.javaPrimitiveType) return true
        if (this == Short::class.javaPrimitiveType) return true
        if (this == Byte::class.javaPrimitiveType) return true
        return false
    }

val KClass<*>.isEnum: Boolean get() = java.isEnum || java.superclass.isEnum

fun Duration.toReadableTime(biggestUnit: DurationUnit = DurationUnit.DAYS, maxUnits: Int = 2, allowMs: Boolean = false): String {
    val units = listOfNotNull(
        DurationUnit.DAYS to this.inWholeDays,
        DurationUnit.HOURS to this.inWholeHours % 24,
        DurationUnit.MINUTES to this.inWholeMinutes % 60,
        DurationUnit.SECONDS to this.inWholeSeconds % 60,
        (DurationUnit.MILLISECONDS to this.inWholeMilliseconds % 1000).takeIf { allowMs },
    )

    val unitNames = listOfNotNull(
        DurationUnit.DAYS to "d",
        DurationUnit.HOURS to "h",
        DurationUnit.MINUTES to "min",
        DurationUnit.SECONDS to "s",
        (DurationUnit.MILLISECONDS to "ms").takeIf { allowMs },
    ).toMap()

    val filteredUnits = units.dropWhile { it.first != biggestUnit }
        .filter { it.second > 0 }
        .take(maxUnits)

    return filteredUnits.joinToString(", ") { (unit, value) ->
        "$value${unitNames[unit]}"
    }.ifEmpty { "0 seconds" }
}

fun <Input : Output, Output : Any> createCacheSlot(
    swapper: RegistryContextSwapper,
    input: Input,
    codecGetter: (Input) -> MapCodec<out Input>,
) : CacheSlot<ClientLevel, Output> {
    return CacheSlot {
        @Suppress("TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        swapper.swapTo(codecGetter(input).codec(), input.unsafeCast(), it.registryAccess()).result().orElse(input.unsafeCast())
    }
}

fun PoseStack.pose(): PoseStack.Pose = this.last()

fun RenderWorldEvent.renderLineBox(
    box: AABB,
    red: Float = 1f, green: Float = 1f, blue: Float = 1f, alpha: Float = 1f,
) {
    Gizmos.cuboid(box, GizmoStyle.stroke(ARGB.colorFromFloat(alpha, red, green, blue), 2f))
}

fun StringWidget.withClickHandler(handler: (Style) -> Unit) : StringWidget {
    this.setComponentClickHandler(handler)
    return this
}

fun MultiLineTextWidget.withClickHandler(handler: (Style) -> Unit): MultiLineTextWidget {
    this.setComponentClickHandler(handler)
    return this
}

fun <T : Any> CycleButtonBuilder(nameFactory: (T) -> Component, value: () -> T): CycleButton.Builder<T> {
    return CycleButton.builder(nameFactory, value)
}
