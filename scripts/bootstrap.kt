import net.minecraft.SharedConstants
import net.minecraft.client.ClientBootstrap
import net.minecraft.server.Bootstrap

fun bootstrap() {
    SharedConstants.tryDetectVersion()
    Bootstrap.bootStrap()
    ClientBootstrap.bootstrap()
    Bootstrap.validate()

}
