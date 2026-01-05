package me.owdding.catharsis.features.pack

import me.owdding.catharsis.Catharsis
import me.owdding.cats.api.CatsEntry
import me.owdding.cats.api.CatsFile
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.*
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.resources.IoSupplier
import java.io.InputStream
import java.nio.file.Path
import java.util.*

class CatsResourceSupplier(path: Path) : Pack.ResourcesSupplier {
    private val file = Catharsis.runCatching("Failed to open cats pack at $path") { CatsFile(path) }

    override fun openPrimary(location: PackLocationInfo): PackResources {
        return CatsPackResources(location, "/", file)
    }

    override fun openFull(location: PackLocationInfo, metadata: Pack.Metadata): PackResources {
        val root = CatsPackResources(location, "/", file)
        val overlays = metadata.overlays.map { CatsPackResources(location, "/$it/", file) }
        return if (overlays.isEmpty()) root else CompositePackResources(root, overlays)
    }
}

class CatsPackResources(
    location: PackLocationInfo,
    private val prefix: String = "/",
    private val file: CatsFile?
) : AbstractPackResources(location) {

    private fun getPathWithPrefix(path: String): String {
        return "$prefix$path"
    }

    override fun getRootResource(vararg elements: String): IoSupplier<InputStream>? {
        return getResource(elements.joinToString("/"))
    }

    override fun getResource(type: PackType, location: Identifier): IoSupplier<InputStream>? {
        return getResource("${type.directory}/${location.namespace}/${location.path}")
    }

    private fun getResource(path: String): IoSupplier<InputStream>? {
        val entry = this.file?.getEntry(getPathWithPrefix(path)) as? CatsEntry.File ?: return null
        return IoSupplier { this.file.getInputStream(entry) }
    }

    override fun listResources(type: PackType, namespace: String, path: String, output: PackResources.ResourceOutput) {
        val prefix = getPathWithPrefix("${type.directory}/$namespace/")
        val directory = file?.getEntry("$prefix$path/") as? CatsEntry.Directory ?: return
        val queue = ArrayDeque<CatsEntry.Directory>()
        queue.add(directory)
        while (queue.isNotEmpty()) {
            val entry = queue.removeFirst()
            for ((name, entry) in entry.entries()) {
                if (entry is CatsEntry.Directory) {
                    queue.add(entry)
                } else if (entry is CatsEntry.File) {
                    val id = Identifier.tryBuild(namespace, name.substring(prefix.length))
                    if (id != null) {
                        output.accept(id) { file.getInputStream(entry) }
                    } else {
                        Catharsis.warn("Invalid path in MTP pack $namespace:${name.removePrefix(prefix)}")
                    }
                }
            }
        }
    }

    override fun getNamespaces(type: PackType): Set<String> {
        val prefix = getPathWithPrefix(type.directory + "/")
        val namespaces = mutableSetOf<String>()
        val rootEntry = file?.getEntry(prefix) as? CatsEntry.Directory ?: return namespaces
        for ((name, entry) in rootEntry.entries()) {
            if (entry is CatsEntry.Directory) {
                val namespace = name.removePrefix(prefix).removeSuffix("/")
                if (Identifier.isValidNamespace(namespace)) {
                    namespaces.add(namespace)
                } else {
                    Catharsis.warn("Non [a-z0-9_.-] character in namespace $namespace in mtp pack")
                }
            }
        }
        return namespaces
    }

    override fun close() {
    }

}

