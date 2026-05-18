---
title: Item
lang: en-US
---

# <Environment type="hypixel"/> Data Types

This page lists all available data types.

All Data Types can be found [here](https://github.com/SkyblockAPI/SkyblockAPI/blob/4.0/src/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/DataTypes.kt).

IDs of Data Types can be found at these locations:
[GenericDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/4.0/src/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/GenericDataTypes.kt),
[LoreDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/4.0/src/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/LoreDataTypes.kt),
[MiningDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/4.0/src/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/MiningDataTypes.kt),
[PersonalAccessoryDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/4.0/src/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/PersonalAccessoryDataTypes.kt).

You can use `/catharsis dev hand_data_types` to view all data types of the item in your hand.

### Notice

Around 95% of data types will not work on items held/worn by other players since Hypixel does not share the custom item data and lore for them.
We can't do anything about that except providing fallbacks for some of them (like `has_skin_fallback` and `has_dye_fallback`), 
but most of them will simply return null or 0 when used on other player's items, so keep that in mind when using them in conditions or ranges.

## Data Type Categories and Comparing

| Category | Conditional | Select | Range |
|----------|-------------|--------|-------|
| Number   | ❌           | ✔️     | ✔️    |
| String   | ❌           | ✔️     | ❌     |
| Boolean  | ✔️          | ✔️     | ❌     |

## Available Number Data Types

Data Types that return any form of number (so Integer, Double, Float, Long, Short, Byte, or Enums ordinals) are available by default.

### Custom Number Data Types

| Data Type                 | Description                                            |
|---------------------------|--------------------------------------------------------|
| `snowballs`               | Returns the current snowballs in the snowball shooter. |
| `dungeon_breaker_charges` | Returns the current charges.                           |
| `fuel`                    | Returns the current drill fuel.                        |
| `water_level`             | Returns the current water level.                       |

## Available String Data Types

Data Types that return a String or Enum names are available by default.

### Custom String Data Types

| Data Type | Description                                  |
|-----------|----------------------------------------------|
| `hook`    | Returns the SkyBlockId of the applied hook   |
| `line`    | Returns the SkyBlockId of the applied line   |
| `sinker`  | Returns the SkyBlockId of the applied sinker |

## Available Boolean Data Types

Data Types that return a Boolean are available by default.

### Custom Boolean Data Types

| Data Type           | Description                                                                                                                      |
|---------------------|----------------------------------------------------------------------------------------------------------------------------------|
| `has_skin_fallback` | Hypixel doesn't share enough item data for other items, this fallbacks to using the item name to determine if a skin is applied. |
| `has_dye_fallback`  | Hypixel doesn't share enough item data for other items, this fallbacks to using the item name to determine if a dye is applied.  |

## Unavailable Data Types

Any Data Type that returns a complex object (so Lists, Maps, Pairs, or custom objects) are unavailable, unless explicitly specified above.
