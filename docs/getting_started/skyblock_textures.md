---
title: SkyBlock Textures
lang: en-US
---

# Replacing SkyBlock Textures

To replace a SkyBlock item texture, you need to create a JSON file in the `assets/skyblock/items/` folder with the id of the SkyBlock item you want to replace. <br>

::: tip
Some items like runes and attributes get special ids, to find the correct id use [`/catharsis dev hand_id`](/dev_tools/commands#catharsis-dev-hand-id)
:::

::: details Replacing the Hyperion with a Diamond Sword
::: code-group

```json [assets/skyblock/items/hyperion.json]
{
    "model": {
        "type": "minecraft:model",
        "model": "minecraft:item/diamond_sword"
    }
}
```
:::


::: details Replacing the Hyperion with a Custom Model
::: code-group

```json [assets/skyblock/items/hyperion.json]
{
    "model": {
        "type": "minecraft:model",
        "model": "<namespace>:item/hyperion"
    }
}
```

```json [assets/&ltnamespace&gt/models/item/hyperion.json]
{
    "parent": "minecraft:item/handheld",
    "textures": {
        "layer0": "<namespace>:item/hyperion"
    }
}
```
:::
