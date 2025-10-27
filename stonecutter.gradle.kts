plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.11-SNAPSHOT" apply false
}
stonecutter active "1.21.10"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    replacements.string("item_holder") {
        direction = eval(current.version, "=1.21.8")
        replace("import net.minecraft.world.entity.ItemOwner", "import net.minecraft.world.entity.LivingEntity as ItemOwner")
    }
}
