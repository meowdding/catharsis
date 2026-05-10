---
title: Item
lang: en-US
---


# Timespans

Timespans can be used to change things based on the time of day.

The definition follows the following scheme

<TreeView>
<span><TypeIcon type="object"/> An <b>Timespan definition</b> object</span>

- <TypeIcon type="string"/> **type**: One of the timespan definition types defined below
- <TypeIcon/> Additional fields depending on the value of type, see the respective timespan type documentation for more details.

</TreeView>

## Supported Types

### Simple (`catharsis:simple`)

A timespan with a fixed start and end.
<Example>

A simple timespan that only matches nighttime.

<<< @/example_pack/assets/your_name_space/catharsis/timespans/is_day.json{json:line-numbers}
</Example>

<TreeView>
<span><TypeIcon type="object"/> Root <b>simple</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:simple`
- <TypeIcon type="int" /> **begin**: The starting point of the timespan in ticks, relative to the day.
- <TypeIcon type="int" /> **end**: The ending point of the timespan in ticks, relative to the day.

</TreeView>

## Included Timespans

<RepoTimespans />
