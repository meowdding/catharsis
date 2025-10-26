package me.owdding.catharsis.utils.tar

import me.owdding.catharsis.Catharsis
import me.owdding.catharsis.utils.tar.TarFileReader.readTar
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.*
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.resources.IoSupplier
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path
import java.util.*
import java.util.zip.GZIPInputStream

class TarResourceSupplier(path: Path) : Pack.ResourcesSupplier {
    private val fileMap = readTar(
        GZIPInputStream(
            FileInputStream(path.toFile()),
        ),
    )

    override fun openPrimary(location: PackLocationInfo): PackResources {
        return TarPackResources(location, "", fileMap)
    }

    override fun openFull(
        location: PackLocationInfo,
        metadata: Pack.Metadata,
    ): PackResources? {
        val rootResources = TarPackResources(location, "", fileMap)
        val overlays = metadata.overlays
        if (overlays.isEmpty()) {
            return rootResources
        } else {
            val subResources = buildList {
                for (overlay in overlays) {
                    this.add(TarPackResources(location, overlay, fileMap))
                }
            }
            return CompositePackResources(
                rootResources,
                subResources,
            )
        }
    }
}

private fun getPathFromLocation(packType: PackType, location: ResourceLocation): String {
    return String.format(Locale.ROOT, "%s/%s/%s", packType.directory, location.namespace, location.path)
}

class TarPackResources(location: PackLocationInfo, val prefix: String, private val fileMap: Map<String, ByteArray>) : AbstractPackResources(location) {

    fun getResource(path: String): IoSupplier<InputStream>? {
        val byteArray = fileMap[path] ?: return null
        return IoSupplier {
            ByteArrayInputStream(byteArray)
        }
    }

    fun addPrefix(resourcePath: String): String = if (prefix.isEmpty()) resourcePath else "$prefix/$resourcePath"

    fun extractNamespace(directory: String, name: String): String {
        if (name.startsWith(directory)) {
            val dirLength = directory.length
            val firstSlash = name.indexOf('/', dirLength)
            return if (firstSlash == -1) name.substring(dirLength) else name.substring(dirLength, firstSlash)
        } else {
            return ""
        }
    }

    override fun getRootResource(vararg elements: String): IoSupplier<InputStream>? {
        return getResource(elements.joinToString("/"))
    }

    override fun getResource(
        packType: PackType,
        location: ResourceLocation,
    ): IoSupplier<InputStream>? {
        return getResource(getPathFromLocation(packType, location))
    }

    override fun listResources(
        packType: PackType,
        namespace: String,
        path: String,
        resourceOutput: PackResources.ResourceOutput,
    ) {
        val namespaceRoot = addPrefix("${packType.directory}/$namespace/")
        val searchDirectory = "$namespaceRoot$path/"
        for ((fileName, fileData) in fileMap) {
            if (fileName.startsWith(searchDirectory)) {
                val resourcePath = fileName.substring(namespaceRoot.length)
                val resourceLocation = ResourceLocation.tryBuild(namespace, resourcePath)
                if (resourceLocation != null) {
                    resourceOutput.accept(resourceLocation) {
                        ByteArrayInputStream(fileData)
                    }
                } else {
                    Catharsis.error("Invalid path in pack: $namespace:$resourcePath, ignoring")
                }
            }
        }
    }

    override fun getNamespaces(type: PackType): Set<String> {
        val namespaces = mutableSetOf<String>()
        val prefixedResourceRoot = addPrefix("${type.directory}/")
        for (fileName in fileMap.keys) {
            val namespace = extractNamespace(prefixedResourceRoot, fileName)
            if (namespace.isNotEmpty()) {
                if (ResourceLocation.isValidNamespace(namespace)) {
                    namespaces.add(namespace)
                } else {
                    Catharsis.warn("Invalid namespace character $namespace in tar")
                }
            }
        }
        return namespaces
    }

    override fun close() {}
}
