---
title: Conditions
lang: en-US
---

# Block Conditions

Block conditions can be used together with a [conditional block replacement](index#conditional-catharsis-conditional) to check properties and types of blocks.

## Conditions

All conditions follow the same schema which can be found below.

<TreeView>
<span><TypeIcon type="object"/> A <b>block condition</b> object</span>

- <TypeIcon type="string"/> **type**: One of the block condition types defined below
- <TypeIcon/> Additional fields depending on the value of type, see the respective block condition type documentation for more details.

</TreeView>

## Supported Types

### Always (`always`)

Always passes.

<TreeView>
<span><TypeIcon type="object"/> Root <b>always</b> object</span>

- <TypeIcon type="string"/> **type**: `always`
</TreeView>

### Never (`never`)

Never passes.

<TreeView>
<span><TypeIcon type="object"/> Root <b>never</b> object</span>

- <TypeIcon type="string"/> **type**: `never`
</TreeView>

### And (`and`)

Only passes if all the sub conditions pass.

<TreeView>
<span><TypeIcon type="object"/> Root <b>and</b> object</span>

- <TypeIcon type="string"/> **type**: `and`
- <TypeIcon type="array"/> **conditions**: A list of block conditions
</TreeView>

### Or (`or`)

Passes if any one of the sub conditions pass.

<TreeView>
<span><TypeIcon type="object"/> Root <b>or</b> object</span>

- <TypeIcon type="string"/> **type**: `or`
- <TypeIcon type="array"/> **conditions**: A list of block conditions
</TreeView>

### Not (`not`)

Inverts the result of the sub condition.

<TreeView>
<span><TypeIcon type="object"/> Root <b>not</b> object</span>

- <TypeIcon type="string"/> **type**: `not`
- <TypeIcon type="object"/> **condition**: A block condition
</TreeView>


### Id (`id`)

Checks if the block matches a specific block id or tag.

<TreeView>
<span><TypeIcon type="object"/> Root <b>id</b> object</span>

- <TypeIcon type="string"/> **type**: `id`
- <TypeIcon type="array"/><TypeIcon type="string"/> **block**: A block predicate, must be one of the following.
    - An id like `minecraft:dirt`
    - A list of ids like `["minecraft:dirt", "minecraft:stone"]`
    - A tag like `#minecraft:dirt`

</TreeView>

### Properties (`properties`)

Checks if the block has a certain property value.

<TreeView>
<span><TypeIcon type="object"/> Root <b>properties</b> object</span>

- <TypeIcon type="string"/> **type**: `properties`
- <TypeIcon type="object"/> **properties**: The properties to match
  - <TypeIcon type="string"/> **&lt;key&gt;**: The property to check, names can be found in the f3 menu.
    - <TypeIcon type="string"/> The value to match, must ALWAYS be formatted as string.

</TreeView>

### Relative (`relative`)

Changes the block context for the sub condition.

<TreeView>
<span><TypeIcon type="object"/> Root <b>relative</b> object</span>

- <TypeIcon type="string"/> **type**: `relative`
- <Position positionType="int" customText> <b>offset</b>: The block to check relative to the current position.</Position>
- <TypeIcon type="object"/> **condition**: The condition to check with the offset applied.
</TreeView>

### In Island (`in_island`)

Checks if the player in on a certain island.

<TreeView>
<span><TypeIcon type="object"/> Root <b>in island</b> object</span>

- <TypeIcon type="string"/> **type**: `in_island`
- <TypeIcon type="string"/> **island**: A skyblock island.
    - <Island/>
</TreeView>

### Timespan (`timespan`)

Checks if a certain [timespan](../miscellaneous/timespans) is true.

<TreeView>
<span><TypeIcon type="object"/> Root <b>timespan</b> object</span>

- <TypeIcon type="string"/> **type**: `timespan`
- <TypeIcon type="string"/> **timespan**: The identifier of the timespan to check.
</TreeView>
