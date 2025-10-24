---
title: Getting Started
lang: en-US
---

# Getting Started

Catharsis uses vanilla [`Item Model Definitions`](https://minecraft.wiki/w/Items_model_definition#select) as the base for custom models.

In the following example we will replace the hyperion with a diamond sword.

::: code-group

```json [assets/skyblock/items/hyperion.json]
{
    "model": {
        "type": "minecraft:model",
        "model": "minecraft:item/diamond_sword.json"
    }
}
```
:::
