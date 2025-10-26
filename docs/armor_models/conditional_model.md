---
title: Conditional Armor Model
lang: en-US
---

# Conditional Armor Model

Similarly to item models, armor models also support the `minecraft:condition` model type.

You can use all item model properties as conditions for armor models as well. 
Though some properties may not make sense for armor, such as `extended_view` or `selected`.
This also means you can use custom Catharsis item properties as conditions for armor models.

You can find a list of all available item model properties on the Minecraft wiki at [Item Model Properties](https://minecraft.wiki/w/Items_model_definition#condition).
Additionally, you can find a list of all Catharsis custom item properties in the [Catharsis Conditional Properties](../item_models/conditional_properties) documentation.

::: details Example: Replacing Diamond Leggings with a Copper Leggings when Sneaking
::: code-group

```json [assets/minecraft/catharsis/armors/diamond_leggings.json]
{
    "model": {
        "type": "minecraft:condition",
        "property": "view_entity",
        "on_true": {
            "type": "minecraft:condition",
            "property": "keybind_down",
            "keybind": "key.sneak",
            "on_true": {
                "type": "catharsis:texture",
                "texture": "minecraft:textures/entity/equipment/humanoid_leggings/copper.png"
            },
            "on_false": {
                "type": "catharsis:texture",
                "texture": "minecraft:textures/entity/equipment/humanoid_leggings/diamond.png"
            }
        },
        "on_false": {
            "type": "catharsis:texture",
            "texture": "minecraft:textures/entity/equipment/humanoid_leggings/diamond.png"
        }
    }
}

```
:::
