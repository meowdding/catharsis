---
title: Texture Armor Model
lang: en-US
---

# Texture Armor Model

Since armor models currently only support texture swapping, the `catharsis:texture` 
model type is used to define the texture to use for the armor.

::: details Example: Replacing Diamond Leggings to Copper Leggings
::: code-group

```json [assets/minecraft/catharsis/armors/diamond_leggings.json]
{
    "model": {
        "type": "catharsis:texture",
        "texture": "minecraft:textures/entity/equipment/humanoid_leggings/copper.png"
    }
}

```
:::
