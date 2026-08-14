---
title: Environmental Modifiers
lang: en-US
---

# <Environment/> Environmental Modifiers

<Version type="1.0.0-beta.22">

Environmental Modifiers allow Texture Packs to modify specific areas/biomes based on an [Area Definition](../models_visuals/block_replacements/areas.md).
These modifications are based off the game's [Environment attribute](https://minecraft.wiki/w/Environment_attribute).

You can define an environmental modifier in `assets/<namespace>/catharsis/environment_modifier/<id>.json`.

## JSON Format

The root object is an Environmental Modifier definition.
There are three main types of modifiers you can define.

### Composite (`composite`)

Allows you to group multiple modifiers under a single condition.

<TreeView>
<span><TypeIcon type="object"/> Root <b>composite</b> modifier object</span>

- <TypeIcon type="string"/> **type**: `composite`
- <TypeIcon type="object"/> **condition**: A [Typeless Condition](#conditions) that determines when the modifiers apply.
- <TypeIcon type="array"/> **modifiers**: A list of [Environmental Modifiers](#json-format) (Environmental Attribute, Biome Effect, or another Composite).

</TreeView>

### Environmental Attribute (`environmental_attribute`)

Modifies vanilla environment attributes (e.g. fog color, sky color).

<TreeView>
<span><TypeIcon type="object"/> Root <b>environmental_attribute</b> modifier object</span>

- <TypeIcon type="string"/> **type**: `environmental_attribute`
- <TypeIcon type="string"/> **attribute**: The attribute identifier to modify (e.g. `minecraft:fog_color`, `minecraft:sky_color`).
- <TypeIcon type="object"/> **provider**: An [Attribute Provider](#attribute-providers) that defines the new value.
- <TypeIcon type="object"/> **condition**: (Optional) A [Condition](#conditions) for this specific attribute.

</TreeView>

### Biome Effect (`biome_effect`)

Modifies specific biome colors like grass, water, or foliage.

<TreeView>
<span><TypeIcon type="object"/> Root <b>biome_effect</b> modifier object</span>

- <TypeIcon type="string"/> **type**: `biome_effect`
- <TypeIcon type="string"/> **effect**: The effect to modify. Can be `water_color`, `foliage_color`, `dry_foliage_color`, or `grass_color`.
- <TypeIcon type="object"/> **provider**: An [Attribute Provider](#attribute-providers) that defines the new color.
- <TypeIcon type="object"/> **condition**: (Optional) A [Condition](#conditions) for this specific effect.

</TreeView>

## Attribute Providers

Attribute Providers determine how a value is overridden or modified.

### Override (`override`)

Overrides the value.

<TreeView>
<span><TypeIcon type="object"/> Root <b>override</b> provider object</span>

- <TypeIcon type="string"/> **type**: `override`
- <TypeIcon type="any"/> **value**: The exact value to override with (e.g. an integer color code).

</TreeView>

### Modified (`modified`)

Modifies the base value.

<TreeView>
<span><TypeIcon type="object"/> Root <b>modified</b> provider object</span>

- <TypeIcon type="string"/> **type**: `modified`
- <TypeIcon type="string"/> **modifier**: The modifier identifier.
- <TypeIcon type="any"/> **argument**: The argument for the modifier.

</TreeView>

## Conditions

Conditions check whether the modifier should be applied at a specific location.

### In Area (`in_area`)

Checks if the position is within a specific Area Definition.

<TreeView>
<span><TypeIcon type="object"/> Root <b>in_area</b> condition object</span>

- <TypeIcon type="string"/> **type**: `in_area`
- <TypeIcon type="string"/> **area**: The identifier of an [Area Definition](../models_visuals/block_replacements/areas.md).

</TreeView>

### True / False (`true`, `false`)

Constant conditions.

<TreeView>
<span><TypeIcon type="object"/> Root <b>true/false</b> condition object</span>

- <TypeIcon type="string"/> **type**: `true` or `false`

</TreeView>


</Version>

<VersionNot type="1.0.0-beta.22">

:::warning

Available with 1.0.0-Beta-22

:::

</VersionNot>
