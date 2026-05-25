---
title: Miscellaneous Items
lang: en-US
---

# <Environment/> Miscellaneous Items

Catharsis allows you to define custom SkyBlock IDs based on the texture data of Player Heads. 
This is useful for assigning specific IDs to custom head items that do not have an ID assigned by Hypixel.

## Defining

Custom miscellaneous items are defined in `assets/catharsis/misc_items.json` within your resource pack.

<Example>

<<< @/example_pack/assets/catharsis/misc_items.json{json:line-numbers}
</Example>

<TreeView>
<span><TypeIcon type="object"/> Root <b>misc_items</b> object</span>

- <TypeIcon type="object"/> **textures**: A map of identifiers to base64 textures.
    - <TypeIcon type="string"/> **&lt;key&gt;**: The identifier for the texture/textures.
      - <TypeIcon type="string"/> A texture or a list of textures used for the id.

</TreeView>

You can also view the remote defined `misc_items.json` on [GitHub](https://github.com/meowdding/catharsis/blob/development/repo/misc_items.json).

:::tip
It is highly recommended to use actual SkyBlock IDs whenever possible. 
Relying on texture data should only be used as a last resort when no other unique identifier is available.
:::

## Usage

Once defined, the custom namespace and location will be used as the item's ID.
