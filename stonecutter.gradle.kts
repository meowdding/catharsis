plugins {
    id("dev.kikugie.stonecutter")
}

@Suppress("UNCHECKED_CAST")
fun <T> Any?.unsafeCast() : T = this as T


ext["latest"] = stonecutter.vcsVersion.version
stonecutter active "26.2"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    Replacements.read(project).replacements.forEach { (name, replacement) ->
        when (replacement) {
            is StringReplacement if replacement.named -> replacements.string(name) {
                direction = eval(current.version, replacement.condition)
                replace(replacement.from, replacement.to)
            }

            is RegexReplacement if replacement.named -> replacements.regex(name) {
                direction = eval(current.version, replacement.condition)
                replace(replacement.regex, replacement.to, replacement.reverseRegex, replacement.reverse)
            }

            is StringReplacement -> replacements.string {
                direction = eval(current.version, replacement.condition)
                replace(replacement.from, replacement.to)
            }

            is RegexReplacement -> replacements.regex {
                direction = eval(current.version, replacement.condition)
                replace(replacement.regex, replacement.to, replacement.reverseRegex, replacement.reverse)
            }
        }
    }
}
