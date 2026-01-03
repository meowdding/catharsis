---
title: Pack Metadata
lang: en-US
---

# Defining Catharsis Pack Metadata

<Example>

TODO

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
  - <TypeIcon type="array"/> **config**: (Optional) A list of config elements for the settings menu.
    - <TypeIcon type="object"/> **Config Element**:
      - <TypeIcon type="string"/> **type**: The type of element (`tab`, `dropdown`, `boolean`, or `separator`).
      - <TypeIcon type="string"/> **id**: (Required for inputs) The unique key for this config option, needs to be `[a-z0-9_.-]+`.
      - <TypeIcon type="string"/> **title**: The display name, supporting both string and JSON text component.
      - <TypeIcon type="string"/> **description**: (Optional) A description for the option, supporting both string and JSON text component.
      - <TypeIcon type="boolean"/> **default**: (Optional) (For `boolean`) The default toggle state, defaults to `false`.
      - <TypeIcon type="array"/> **options**: (For `dropdown`) A list of selectable values.
        - <TypeIcon type="object"/> **Dropdown Option**:
          - <TypeIcon type="string"/> **value**: The internal ID used for conditions, needs to be `[a-z0-9_.-]+`.
          - <TypeIcon type="string"/> **text**: The display label for the user.
          - <TypeIcon type="boolean"/> **default**: (Optional) Whether this option is selected by default, defaults to `false`.

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

This metadata allows you to specify important information about your Catharsis pack, including its unique identifier, version, dependencies on other mods, and configuration options for users.


## Fabric Overlays Explanation

Pack overlays, (or also called Minipacks by a few), are a system provided by Fabric themselves, just not documented anywhere.

They allow resource packs to conditionally load sub-folders based on certain conditions (or always), providing a way to have modular resource packs or just organised packs.
