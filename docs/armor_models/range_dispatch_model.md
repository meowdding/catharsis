---
title: Range Dispatch Armor Model
lang: en-US
---

# Range Dispatch Armor Model

Similarly to item models, armor models also support the `minecraft:range_dispatch` model type.

You can use all item model numeric properties as numeric properties for armor models as well. 
Though some properties may not make sense for armor, such as `use_duration` or `count`.
This also means you can use custom Catharsis item properties as range properties for armor models.

You can find a list of all available item model properties on the Minecraft wiki at [Item Model Properties](https://minecraft.wiki/w/Items_model_definition#range_dispatch).
Additionally, you can find a list of all Catharsis custom item properties in the [Catharsis Range Properties](../item_models/range_properties) documentation.

::: details Example: Replacing Diamond Leggings when the moon is more than half full to Copper Leggings
::: code-group

```json [assets/minecraft/catharsis/armors/diamond_leggings.json]
{
    "model": {
        "type": "minecraft:range_dispatch",
        "property": "time",
        "source": "moon_phase",
        "wobble": false,
        "entries": [
            {
                "threshold": 0.5,
                "model": {
                    "type": "catharsis:texture",
                    "texture": "minecraft:textures/entity/equipment/humanoid_leggings/copper.png"
                }
            }
        ],
        "fallback": {
            "type": "catharsis:texture",
            "texture": "minecraft:textures/entity/equipment/humanoid_leggings/diamond.png"
        }
    }
}
```
:::
