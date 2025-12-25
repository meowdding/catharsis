---
title: Item
lang: en-US
---

# Select Item Properties

This page lists all custom select item properties that are added ontop of the vanilla ones.

### `catharsis:datatype`

Returns item specific data like, rarity, reforge, fuel...

#### Additional fields

- `data_type`, defines the data type to use. [all supported types](../miscellaneous/data_types.md)


### `catharsis:skyblock_island`

Returns the current skyblock island the player is on. <br>
Names are equal to the value of `mode` in `/locraw`

### `catharsis:skyblock_area`

Returns the current skyblock area the player is in. <br>
Names are equal to the scoreboard line.

### `catharsis:owner_uuid`

Returns the uuid of the entity associated with the item.

### `catharsis:dungeon_class`

Returns the dungeon class of the player who owns the item. <br>
Values can be `archer`, `berserker`, `healer`, `mage` or `tank`, in case there is no class associate with the player it returns null.


### `catharsis:skyblock_season`

Returns the current season.
Possible values are can be found [here](https://github.com/SkyblockAPI/SkyblockAPI/blob/a1be281b5f98f2feb667c5ba33d3e4860ab6b4a4/src/main/kotlin/tech/thatgravyboat/skyblockapi/api/datetime/SkyBlockSeason.kt#L5)
