# Schemas

A list of schemas that are used somewhere in catharsis.

### Bounding Box

A bounding box consists of [two 3d int vectors](#vector-3-position), a min and a max one.
Below are all allowed notations for a bounding box.
```json
[<min>, <max>]
{
  "min": <position>,
  "max": <position>
}
[<minX>,<minY>,<minZ>,<maxX>,<maxY>,<maxZ>]
``` 

### Vector 3 (Position)

A vector 3 is just any position in 3d space.
There are multiple vector types, we mainly differentiate between float and int. 
Below are all allowed notations for a vector.
```json
[x,y,z]
{
  "x": <x>,
  "y": <y>,
  "z": <z>
}
"<x>:<y>:<z>"
```

### Skyblock Island

Skyblock islands are directly linked to the hypixel skyblock islands.
They can be either the value of `mode` in `/locraw`, or one of the [enum values](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/location/SkyBlockIsland.kt)
