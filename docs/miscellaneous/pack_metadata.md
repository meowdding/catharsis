---
title: Pack Metadata
lang: en-US
---

# Defining Catharsis Pack Metadata

Catharsis metadata allows your pack to have a unique ID, declare dependencies, and provide a configuration menu for users.

<Example>

Example of a `pack.mcmeta` with a configuration menu and version dependencies.

<<< @/example_pack/pack.mcmeta{json:line-numbers}
</Example>

<TreeView>
  <span><TypeIcon type="object"/> A <b>.mcmeta ResourcePack</b> object</span>

- <TypeIcon type="object"/> **catharsis:pack/v1**: The Catharsis pack metadata.
  - <TypeIcon type="string"/> **id**: A unique id for the pack, needs to be `[a-z0-9_.-]+`.
  - <TypeIcon type="string"/> **version**: The version of the pack.
  - <TypeIcon type="object"/> **dependencies**: (Optional) A map of required mods.
    - <TypeIcon type="string"/> **&lt;key&gt;**: The id of the mod.
      - <TypeIcon type="string"/> The version range (e.g., `>=1.0.0`).
  - <TypeIcon type="boolean"/> **pack_required_for_config**: (Optional) Whether the pack is required for the config menu to show up, defaults to `false`.
  - <TypeIcon type="array"/> **config**: (Optional) A list of config elements for the settings menu.

- <TypeIcon type="object"/> **fabric:overlays**: (Optional) A system for conditional resource loading.
  - <TypeIcon type="array"/> **entries**: A list of overlay definitions.
    - <TypeIcon type="object"/> **Overlay Entry**:
      - <TypeIcon type="string"/> **directory**: The sub-folder within the pack to apply if conditions are met, needs to be `[a-z0-9_.-]+`.
      - <TypeIcon type="object"/> **condition**: The logic required to enable this overlay.
        - <TypeIcon type="string"/> **condition**: The condition type (e.g., `catharsis:config`, `fabric:not`, ...).
        - <TypeIcon type="string"/> **pack**: The ID of the pack containing the config.
        - <TypeIcon type="string"/> **id**: The ID of the config option to check.
        - <TypeIcon type="string"/> **value**: (Optional) The specific value to match (used for dropdowns).

</TreeView>

## Catharsis Metadata Explanation

Catharsis pack metadata is defined within the `catharsis:pack/v1` object inside the `pack.mcmeta` file of your resource pack.

This metadata allows you to specify important information about your Catharsis pack, including its unique identifier, version, dependencies on other mods, and configuration options
for users.

The config can also be stored in `config.catharsis.json` inside the root of your pack. Using this will override any configurations defined in the `pack.mcmeta` file.
The Fabric Overlays are still required in the `pack.mcmeta`.

## Config Element Definitions

Each element in the `config` array must define a `type`. Elements that store values (like `boolean` or `color`) require a unique `id`.

### Tab (`tab`)
Used to group related configuration options into separate navigation tabs.

<TreeView>

- <TypeIcon type="string"/> **type**: `tab`
- <TypeIcon type="string"/> **title**: The display name of the tab.
- <TypeIcon type="array"/> **options**: A list of config elements to display within this tab.

</TreeView>

### Boolean (`boolean`)
A toggle switch for on/off settings.

<TreeView>

- <TypeIcon type="string"/> **type**: `boolean`
- <TypeIcon type="string"/> **id**: The unique key for this option.
- <TypeIcon type="string"/> **title**: The display name.
- <TypeIcon type="string"/> **description**: (Optional) A default description.
- <TypeIcon type="object"/> **descriptions**: (Optional) A map of `"true"` or `"false"` to specific description components.
- <TypeIcon type="boolean"/> **default**: The default toggle state, defaults to `false`.

</TreeView>

### Dropdown (`dropdown`)
A menu for selecting a single value from a list of options.

<TreeView>

- <TypeIcon type="string"/> **type**: `dropdown`
- <TypeIcon type="string"/> **id**: The unique key for this option.
- <TypeIcon type="string"/> **title**: The display name.
- <TypeIcon type="string"/> **description**: (Optional) A default description.
- <TypeIcon type="object"/> **descriptions**: (Optional) A map of option values to specific description components.
- <TypeIcon type="array"/> **options**: A list of selectable values.
  - <TypeIcon type="object"/> **Dropdown Option**:
    - <TypeIcon type="string"/> **value**: The internal ID used for conditions.
    - <TypeIcon type="string"/> **text**: The display label.
    - <TypeIcon type="boolean"/> **default**: (Optional) Whether this option is selected by default.

</TreeView>

### Select (`select`)
A list based selection that can support single or multiple choices.

<TreeView>

- <TypeIcon type="string"/> **type**: `select`
- <TypeIcon type="string"/> **id**: The unique key for this option.
- <TypeIcon type="string"/> **title**: The display name.
- <TypeIcon type="string"/> **description**: (Optional) A default description.
- <TypeIcon type="boolean"/> **single**: (Optional) If `true`, only one item can be selected. Defaults to `false`.
- <TypeIcon type="array"/> **options**: A list of selectable entries.
  - <TypeIcon type="object"/> **Select Entry**:
    - <TypeIcon type="string"/> **value**: The internal ID.
    - <TypeIcon type="string"/> **text**: The label. Can be a string or an object with `selected` and `unselected` variants.
    - <TypeIcon type="boolean"/> **selected**: (Optional) Whether the entry is checked by default. Defaults to `false`.

</TreeView>

### Color (`color`)
A color picker tool that stores the value as a decimal integer.

<TreeView>

- <TypeIcon type="string"/> **type**: `color`
- <TypeIcon type="string"/> **id**: The unique key for this option.
- <TypeIcon type="string"/> **title**: The display name.
- <TypeIcon type="string"/> **description**: The description for the color picker.
- <TypeIcon type="int"/> **default**: The default color as an integer.
- <TypeIcon type="boolean"/> **alpha**: (Optional) Whether to enable transparency (RGBA) support. Defaults to `false`.

</TreeView>

### Information (`information`)
A text block for text.

<TreeView>

- <TypeIcon type="string"/> **type**: `information`
- <TypeIcon type="string"/> **title**: The header text.
- <TypeIcon type="string"/> **description**: The body content.

</TreeView>

### Separator (`separator`)
A visual divider used to organize the menu, same as `information` just with a divider at the bottom.

<TreeView>

- <TypeIcon type="string"/> **type**: `separator`
- <TypeIcon type="string"/> **title**: (Optional) Text displayed on the divider.
- <TypeIcon type="string"/> **description**: (Optional) A description for the divider.

</TreeView>

---

## Fabric Overlays Explanation

Pack overlays, (or also called Minipacks by a few), are a system provided by Fabric themselves.

Their docs are located [here](https://wiki.fabricmc.net/drafts:resourceconditions)

### Custom Overlay Conditions

#### `catharsis:config`

<TreeView>
  <span><TypeIcon type="object"/> A <b>Fabric Condition Object</b> object</span>

- <TypeIcon type="string"/> **condition**: `catharsis:config`
- <TypeIcon type="string"/> **pack**: The ID of the pack containing the config.
- <TypeIcon type="string"/> **id**: The ID of the config option to check.
- <TypeIcon type="string"/> **value**: (Optional) The specific value to match (used for dropdowns).

</TreeView>

#### `catharsis:version`

<TreeView>
  <span><TypeIcon type="object"/> A <b>Fabric Condition Object</b> object</span>

- <TypeIcon type="string"/> **condition**: `catharsis:version`
- <TypeIcon type="string"/> **type**: Either `MINECRAFT` or `PACK_FORMAT`
- <TypeIcon type="string"/> **minecraftPredicate**: __Required if MINECRAFT__, uses [Fabric's System](https://wiki.fabricmc.net/documentation:fabric_mod_json_spec?s[]=version)
- <TypeIcon type="object"/> **packFormatRange**: __Required if PACK_FORMAT__
    - <TypeIcon type="int"/> **min_inclusive**: The minimum pack format version.
    - <TypeIcon type="int"/> **max_inclusive**: The maximum pack format version.

</TreeView>
