---
title: Pack Configuration
lang: en-US
---

# <Environment/> Config Element Definitions

Your config can either be defined inside the `pack.mcmeta` file under the `catharsis:pack/v1` object or in a separate `config.catharsis.json` file in the root of your pack. 
The latter will override any config defined in the `pack.mcmeta`.

Each element in the `config` array must define a `type`. Elements that store values (like `boolean` or `color`) require a unique `id`.

## Tab (`tab`)
Used to group related configuration options into separate navigation tabs.

<TreeView>

- <TypeIcon type="string"/> **type**: `tab`
- <TypeIcon type="string"/> **title**: The display name of the tab.
- <TypeIcon type="array"/> **options**: A list of config elements to display within this tab.

</TreeView>

## Boolean (`boolean`)
A toggle switch for on/off settings.

<TreeView>

- <TypeIcon type="string"/> **type**: `boolean`
- <TypeIcon type="string"/> **id**: The unique key for this option.
- <TypeIcon type="string"/> **title**: The display name.
- <TypeIcon type="string"/> **description**: (Optional) A default description.
- <TypeIcon type="object"/> **descriptions**: (Optional) A map of `"true"` or `"false"` to specific description components.
- <TypeIcon type="boolean"/> **default**: The default toggle state, defaults to `false`.

</TreeView>

## Dropdown (`dropdown`)
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

## Select (`select`)
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

## Color (`color`)
A color picker tool that stores the value as a decimal integer.

<TreeView>

- <TypeIcon type="string"/> **type**: `color`
- <TypeIcon type="string"/> **id**: The unique key for this option.
- <TypeIcon type="string"/> **title**: The display name.
- <TypeIcon type="string"/> **description**: The description for the color picker.
- <TypeIcon type="int"/> **default**: The default color as an integer.
- <TypeIcon type="boolean"/> **alpha**: (Optional) Whether to enable transparency (RGBA) support. Defaults to `false`.

</TreeView>

### Custom Item Tints

Can be used to give tints to items:

<TreeView>
<span><TypeIcon type="object"/> List of <b>tints</b> inside an <a href="https://minecraft.wiki/w/Items_model_definition#model">item model definition</a></span>

- <TypeIcon type="string"/> **type**: `catharsis:config`
- <TypeIcon type="string"/> **pack**: The id of the pack to check the config entry for.
- <TypeIcon type="string"/> **id**: The id of the color config entry to check.

</TreeView>

## Information (`information`)
A text block for text.

<TreeView>

- <TypeIcon type="string"/> **type**: `information`
- <TypeIcon type="string"/> **title**: The header text.
- <TypeIcon type="string"/> **description**: The body content.

</TreeView>

## Separator (`separator`)
A visual divider used to organize the menu, same as `information` just with a divider at the bottom.

<TreeView>

- <TypeIcon type="string"/> **type**: `separator`
- <TypeIcon type="string"/> **title**: (Optional) Text displayed on the divider.
- <TypeIcon type="string"/> **description**: (Optional) A description for the divider.

</TreeView>
