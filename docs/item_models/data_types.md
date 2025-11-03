---
title: Item
lang: en-US
---

# Data Types

TODO: which ones can be Conditional, Select and Range?????

This page lists all available data types.

All Data Types can be found [here](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/DataTypes.kt).

IDs of Data Types can be found at these locations:
[GenericDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/GenericDataTypes.kt),
[LoreDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/LoreDataTypes.kt),
[MiningDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/MiningDataTypes.kt),
[PersonalAccessoryDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/PersonalAccessoryDataTypes.kt).

## Available Number Data Types

Any Data Types that return any form of number (so Integer, Double, Float, Long, Short, Byte) are available to you.

### Custom Number Data Types

| Data Type              | Description                                            |
|------------------------|--------------------------------------------------------|
| Snowballs              | Returns the current snowballs in the snowball shooter. |
| Dungeonbreaker Charges | Returns the current charges.                           |
| Drill Fuel             | Returns the current drill fuel.                        |

## Available String Data Types

Any Data Types that return a String are available to you.

### Custom String Data Types

| Data Type | Description                                  |
|-----------|----------------------------------------------|
| Hook      | Returns the SkyBlockId of the applied hook   |
| Line      | Returns the lore line at the applied line    |
| Sinker    | Returns the SkyBlockId of the applied sinker |

## Available Boolean Data Types

Data Types that return a Boolean are available by default.

## Unavailable Data Types

Any Data Type that returns a complex object (so Lists, Maps, Pairs, or custom objects) are, unless custom handled above, unavailable to you.
