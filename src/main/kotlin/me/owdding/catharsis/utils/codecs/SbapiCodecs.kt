package me.owdding.catharsis.utils.codecs

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import me.owdding.catharsis.generated.EnumCodec
import me.owdding.ktcodecs.IncludedCodec
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

object SbapiCodecs {

    @IncludedCodec val skyblockIslandCodec: Codec<SkyBlockIsland> = Codec.either(
        EnumCodec.forKCodec(SkyBlockIsland.entries.toTypedArray()),
        Codec.STRING.xmap(
            { id -> SkyBlockIsland.getById(id) },
            { property -> property?.id }
        )
    ).xmap({ Either.unwrap(it) }, { Either.left(it) })

}
