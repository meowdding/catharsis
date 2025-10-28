# Schemas

A list of schemas that are used somewhere in catharsis.

### Bounding Box

> A bounding box consists of [two 3d int vectors](#vector-3-position), a min and a max one.
> Below are all allowed notations for a bounding box.
> <br/>
> <br/>
>
> <TreeView>
> <span><TypeIcon type="object"/> Root <b>bounding box</b> object</span>
> 
> - <TypeIcon type="object"/> **min**: The min [position](#position)
> - <TypeIcon type="object"/> **max**: The max [position](#position)
> 
> </TreeView>
> 
> #### Alternative notations
> ```json
> [<min>, <max>]
> [<minX>, <minY>, <minZ>, <maxX>, <maxY>, <maxZ>]
> ``` 

### Vector 3 (Position)

<TreeView>
<ul>
<BoundingBox/>
</ul>
</treeView>
A vector 3 is just any position in 3d space.
There are multiple vector types, we mainly differentiate between float and int. 
Below are all allowed notations for a vector.
<br/>
<br/>

<TreeView>
<TypeIcon type="empty"/>
- <TypeIcon type/>
   - <TypeIcon type="float"/> **min**: The x coordinate
   - <TypeIcon type="float"/> **max**: The y coordinate
   - <TypeIcon type="float"/> **max**: The z coordinate
</TreeView>

 #### Alternative notations
 ```json
 [<x>, <y>, <z>]
 "<x>:<y>:<z>"
 ```

### Skyblock Island

> Skyblock islands are directly linked to the hypixel skyblock islands.
> They can be either the value of `mode` in `/locraw`, or one of the [enum values](https://github.com/SkyblockAPI/SkyblockAPI/blob/2.0/src/common/main/kotlin/tech/thatgravyboat/skyblockapi/api/location/SkyBlockIsland.kt)
