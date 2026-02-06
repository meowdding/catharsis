package me.owdding.catharsis.features.item

import me.owdding.catharsis.PreLoadModule
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GenericDataTypes
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@PreLoadModule
object CatharsisDataTypes {

    val HAS_SKIN_FALLBACK = DataType.of("has_skin_fallback") {
        if (GenericDataTypes.HELMET_SKIN.factory(it) != null) true
        else it.hoverName.stripped.endsWith("✦").takeIf { name -> name }
    }

    val HAS_DYE_FALLBACK = DataType.of("has_dye_fallback") {
        if (GenericDataTypes.APPLIED_DYE.factory(it) != null) true
        else it.hoverName.stripped.startsWith("✿").takeIf { name -> name }
    }

}
