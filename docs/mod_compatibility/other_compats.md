---
title: Mod Compatibility Overview
lang: en-US
---

# <Environment /> Other Mod Compatibility

### Block Replacements Jade Integration

When defining a [Virtual State](../models_visuals/block_replacements/virtual_block_states.md) for a custom block,
you can give these fake blocks their own display in Jade by providing custom text in the `display` key.

::: warning
Jade handles some blocks, like infested blocks, a bit weird and shows the normal variant,
which you can't override as off now.
:::

### Applying a Gui Definition based on Mod Configs

When defining a [Gui Definition](../ui_text/guis/definitions.md), you can use `catharsis:external_mod_config` to (not) apply
specific Gui Definitions if a different mod has a specific config toggle set to specific value.

::: warning
If a mod does not instantly save their config and instead saves on game close or similar,
this cannot work as it reads the config file.
:::

<Version type="1.0.0-beta.21">

### Gui Modifier to hide other Mod Elements

Using a [Gui Modifier](../ui_text/guis/modifiers.md) you can use `hiddenModElements` to hide Gui Elements of other mods,
if they defined an ID for these.

Requires using the `hidden_mod_elements` feature in [IMC](imc.md).

</Version>


