---
title: Item
lang: en-US
---

# Data Types

This page lists all available data types.

All Data Types can be found [here](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/DataTypes.kt).

IDs of Data Types can be found at these locations:
[GenericDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/GenericDataTypes.kt),
[LoreDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/LoreDataTypes.kt),
[MiningDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/MiningDataTypes.kt),
[PersonalAccessoryDataTypes](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/datatype/defaults/PersonalAccessoryDataTypes.kt).

## Data Type Categories and Comparing

| Category | Conditional | Select | Range |
|----------|-------------|--------|-------|
| Number   | ❌          | ✔️     | ✔️    |
| String   | ❌          | ✔️      | ❌     |
| Boolean  | ✔️          | ✔️      | ❌     |

## Available Number Data Types

Data Types that return any form of number (so Integer, Double, Float, Long, Short, Byte, or Enums ordinals) are available by default.

### Custom Number Data Types

| Data Type                | Description                                            |
|--------------------------|--------------------------------------------------------|
| `snowballs`              | Returns the current snowballs in the snowball shooter. |
| `dungeonbreaker_charges` | Returns the current charges.                           |
| `drill_fuel`             | Returns the current drill fuel.                        |

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

## Unavailable Data Types

Any Data Type that returns a complex object (so Lists, Maps, Pairs, or custom objects) are unavailable, unless explicitly specified above.
