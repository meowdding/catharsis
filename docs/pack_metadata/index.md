---
title: Pack Metadata
lang: en-US
---

# <Environment/> Defining Katharsis Pack Metadata

Katharsis metadata allows your pack to have a unique ID, declare dependencies, and provide a configuration menu for users.

<Example>

Example of a `pack.mcmeta` with a configuration menu and version dependencies.

<<< @/example_packs/TODO/pack.mcmeta{json:line-numbers}
</Example>

<TreeView>
  <span><TypeIcon type="object"/> A <b>.mcmeta ResourcePack</b> object</span>

- <TypeIcon type="object"/> **katharsis:pack/v1**: The Katharsis pack metadata.
  - <TypeIcon type="string"/> **id**: A unique id for the pack, needs to be `[a-z0-9_.-]+`.
  - <TypeIcon type="string"/> **version**: The version of the pack.
  - <TypeIcon type="object"/> **dependencies**: (Optional) A map of required mods.
    - <TypeIcon type="string"/> **&lt;key&gt;**: The id of the mod.
      - <TypeIcon type="string"/> The version range (e.g., `>=1.0.0`).
  - <TypeIcon type="boolean"/> **pack_required_for_config**: (Optional) Whether the pack is required for the config menu to show up, defaults to `false`.
  - <TypeIcon type="boolean"/> **disable_derived_ids**: (Optional) Whether to disable derived SkyBlock IDs, defaults to `false`. 
    Read more [here](/getting_started/skyblock_textures#derived-ids)
  - <TypeIcon type="string"/><TypeIcon type="object"/> **selected_title**: (Optional) A custom title to use when the Pack is enabled. Can be a string or a text component.
  - <TypeIcon type="string"/><TypeIcon type="object"/> **selected_description**: (Optional) A custom description to use when the Pack is enabled. Can be a string or a text component.
  - <TypeIcon type="array"/> **config**: (Optional) A list of config elements for the settings menu, see more [here](/pack_metadata/config.md).

- <TypeIcon type="object"/> **fabric:overlays**: (Optional) A system for conditional resource loading.
  - <TypeIcon type="array"/> **entries**: A list of overlay definitions.
    - <TypeIcon type="object"/> **Overlay Entry**:
      - <TypeIcon type="string"/> **directory**: The sub-folder within the pack to apply if conditions are met, needs to be `[a-z0-9_.-]+`.
      - <TypeIcon type="object"/> **condition**: A Fabric or Katharsis Overlay Condition, see more [here](/pack_metadata/overlays.md).

</TreeView>

## Katharsis Metadata Explanation

Katharsis pack metadata is defined within the `katharsis:pack/v1` object inside the `pack.mcmeta` file of your resource pack.

This metadata allows you to specify important information about your Katharsis pack, including its unique identifier, version, dependencies on other mods, and configuration options
for users.

The config can also be stored in `config.katharsis.json` inside the root of your pack. Using this will override any configurations defined in the `pack.mcmeta` file.
The Fabric Overlays are still required in the `pack.mcmeta`.
