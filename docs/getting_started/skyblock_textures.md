---
title: SkyBlock Textures
lang: en-US
---

# <Environment type="hypixel"/> Replacing SkyBlock Textures

To replace a SkyBlock item texture, you need to create a JSON file in the `assets/skyblock/items/` folder with the ID of the SkyBlock item you want to replace.<br>
ID's containing a `:` have been replaced with `.` to allow for retexturing items like Rose Bushes (`DOUBLE_PLANT:4` -> `double_plant.4.json`)

::: tip
Some items like runes, attributes, enchantments, pets, potions get special sub identifiers.
To quickly find the resolved identifier of any item in your hand, use [`/catharsis dev hand_id`](/dev_tools/commands#catharsis-dev-hand-id).
:::

## Special Sub Identifiers

Because items like attributes, enchantments, pets, potions, and runes share generic base item IDs, 
Catharsis uses their extra data properties to generate custom sub identifiers:

| Item Type          | Identifier                              | Example Logic                                                     |
|:-------------------|:----------------------------------------|:------------------------------------------------------------------|
| **Standard Items** | `skyblock:items/<id>.json`              | Resolved using the base Hypixel ID.                               |
| **Attributes**     | `skyblock:items/attributes/<id>.json`   | Uses the id defined in the `attributes` object on items.          |
| **Enchantments**   | `skyblock:items/enchantments/<id>.json` | Uses the id used in the `enchantments` object on items.           |
| **Pets**           | `skyblock:items/pets/<id>.json`         | Uses the `type` value inside the `petInfo` object.                |
| **Potions**        | `skyblock:items/potions/<id>.json`      | Uses the id defined in `potion_type` or `water` for water bottles |
| **Runes**          | `skyblock:items/runes/<id>.json`        | Uses the id used in the `runes` object on items.                  |

## Derived IDs

Some Gui Items sometimes don't have a SkyBlock ID while clearly representing an actual item.
These **id-less** items get assigned a **Derived ID** based on their context.

Resource packs have the ability to not use these types of IDs. If **every** loaded Catharsis resource pack specifies `"disableDerivedIds": true` in their `pack.mcmeta` file , 
retexturing for all derived IDs will be completely disabled.

### Derived ID Example

- Runes inside the rune sack get their actual ID with the rune level of 0.
- Enchantments inside the hex get their actual ID with the enchantment level of 0.
- ... and way more, check out all IdResolvers [here](https://github.com/SkyblockAPI/SkyblockAPI/tree/4.0/src/main/kotlin/tech/thatgravyboat/skyblockapi/api/remote/api/resolvers).

---

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
