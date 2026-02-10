import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps

private val gson = GsonBuilder().setPrettyPrinting().create()
private val thousandsPlace = listOf("", "M", "MM", "MMM")
private val hundredsPlace = listOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
private val tensPlace = listOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
private val onesPlace = listOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")

fun Int.toRomanNumeral(): String {
    return thousandsPlace[this / 1000] + hundredsPlace[this % 1000 / 100] + tensPlace[this % 100 / 10] + onesPlace[this % 10]
}

fun <T> T.toJson(codec: Codec<T>): JsonElement = codec.encodeStart(JsonOps.INSTANCE, this).getOrThrow()
fun JsonElement.prettyPrint(): String = gson.toJson(this)

fun main() {
    bootstrap()
    skills()
    collections()
    warps()
}
