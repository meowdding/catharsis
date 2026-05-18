---
title: Item Models
lang: en-US
next: # we need to hardcode these bc the "next page" button is broken on index pages
    text: Select Properties
    link: /item_models/select_properties
prev:
    text: Skyblock Texture
    link: /getting_started/skyblock_textures
---

# <Environment/> Item Models

Catharsis extends the vanilla Minecraft item model system by adding custom model conditions. These can be used within your `assets/skyblock/items/<id>.json` files or any
standard item model definition.

## Custom Model Types

### Redirect (`catharsis:redirect`)

Changes the context of the model update to a different equipment slot. This allows an item to render based on what is held in another slot (e.g., a shield rendering differently
based on the sword in the main hand).

<TreeView>
<span><TypeIcon type="object"/> Root <b>redirect</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:redirect`
- <TypeIcon type="string"/> **slot**: The slot to redirect to. One of `mainhand`, `offhand`, `feet`, `legs`, `chest`, `head`, `body`, or `saddle`.
- <TypeIcon type="object"/> **model**: The [item model](https://minecraft.wiki/w/Items_model_definition) to render using the redirected context.

</TreeView>

### Fallthrough (`catharsis:fallthrough`)

Allows a model layer to "fall through" to the next available model in the resource pack stack. This is useful for overlay packs that only want to change specific parts of an item's
rendering without completely replacing the base model.

<TreeView>
<span><TypeIcon type="object"/> Root <b>fallthrough</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:fallthrough`

</TreeView>

### Glint (`catharsis:glint`)

Forces a specific model (or part of a model) to either always have the enchantment glint or never have it, regardless of the item's enchantments.

<TreeView>
<span><TypeIcon type="object"/> Root <b>glint</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:glint`
- <TypeIcon type="boolean"/> **glint**: Whether to apply the enchantment glint.
- <TypeIcon type="object"/> **model**: The [item model](https://minecraft.wiki/w/Items_model_definition) to which the glint setting should be applied.

</TreeView>
