@file:Suppress("UnstableApiUsage")


plugins {
    id("fabric-loom")
    kotlin("jvm") version "2.2.20"
    `versioned-catalogues`
}

//if (stonecutter.active == stonecutter.current) sourceSets {
//    main.configure{
//        kotlin.srcDirs(rootProject.layout.projectDirectory.dir("src/main/kotlin"))
//        java.srcDirs(rootProject.layout.projectDirectory.dir("src/main/java"))
//        resources.srcDirs(rootProject.layout.projectDirectory.dir("src/main/resources"))
//        println(kotlin.srcDirs)
//    }
//}

repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven("https://maven.teamresourceful.com/repository/maven-public/", "tech.thatgravyboat", "me.owdding")
    mavenCentral()
}

dependencies {
    attributesSchema {
        attribute(Attribute.of("earth.terrarium.cloche.minecraftVersion", String::class.java)) {
            disambiguationRules.add(ClocheDisambiguationRule::class) {
                params(versionedCatalog.versions.getOrFallback("sbapi-mc-version", "minecraft").toString())
            }
        }
    }
    minecraft(versionedCatalog["minecraft"])
    mappings(loom.layered {
        officialMojangMappings()
        parchment(variantOf(versionedCatalog["parchment"]) {
            artifactType("zip")
        })
    })
    modImplementation(libs.skyblockapi)
    //modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

    //fapi("fabric-lifecycle-events-v1", "fabric-resource-loader-v0", "fabric-content-registries-v0")
}
