---
title: Mod Compatibility Overview
lang: en-US
---

# <Environment /> Mod Compatibility with Katharsis

Katharsis features an API allowing other mods to interact with its retexturing.
If you have a mod that renders custom items on the GUI, you can give these items custom ids using the Inter Mod Communication (IMC) system.

## Mod Dependency Declaration

If your Texture Pack relies on more dependencies than just Katharsis,
you can set this Mod as a dependency which will make the pack not load if the Mod is not installed by adding the following inside their `pack.mcmeta` files using the `dependencies`
map:

```json
"katharsis:pack/v1": {
...
"dependencies": {
"some_other_mod": ">=1.1.0"
}
}
```

