---
title: Item Models
lang: en-US
---

# <Environment/> Item Models

Katharsis extends the vanilla Minecraft [`Item Model Definitions`](https://minecraft.wiki/w/Items_model_definition#select) system by adding custom model conditions.
These can be used within your `assets/skyblock/items/<id>.json` files or any standard item model definition.
Please try to understand the vanilla system before asking Katharsis specific questions.
You can join the Discord and ask in `#katharsis` about Minecraft's System or Katharsis' extensions.

## Custom Model Types

### Redirect (`katharsis:redirect`)

Changes the context of the model update to a different equipment slot. This allows an item to render based on what is held in another slot (e.g., a shield rendering differently
based on the sword in the main hand).

<TreeView>
<span><TypeIcon type="object"/> Root <b>redirect</b> object</span>

- <TypeIcon type="string"/> **type**: `katharsis:redirect`
- <TypeIcon type="string"/> **slot**: The slot to redirect to. One of `mainhand`, `offhand`, `feet`, `legs`, `chest`, `head`, `body`, or `saddle`.
- <TypeIcon type="object"/> **model**: The [item model](https://minecraft.wiki/w/Items_model_definition) to render using the redirected context.

</TreeView>

### Fallthrough (`katharsis:fallthrough`)

Allows a model layer to "fall through" to the next available model in the resource pack stack. This is useful for overlay packs that only want to change specific parts of an item's
rendering without completely replacing the base model.

<TreeView>
<span><TypeIcon type="object"/> Root <b>fallthrough</b> object</span>

- <TypeIcon type="string"/> **type**: `katharsis:fallthrough`

</TreeView>

### Glint (`katharsis:glint`)

Forces a specific model (or part of a model) to either always have the enchantment glint or never have it, regardless of the item's enchantments.

<TreeView>
<span><TypeIcon type="object"/> Root <b>glint</b> object</span>

- <TypeIcon type="string"/> **type**: `katharsis:glint`
- <TypeIcon type="boolean"/> **glint**: Whether to apply the enchantment glint.
- <TypeIcon type="object"/> **model**: The [item model](https://minecraft.wiki/w/Items_model_definition) to which the glint setting should be applied.

</TreeView>
