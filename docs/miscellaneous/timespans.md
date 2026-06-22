---
title: Item
lang: en-US
---


# <Environment/> Timespans

Timespans can be used to change things based on the time of day.

The definition follows the following scheme

<TreeView>
<span><TypeIcon type="object"/> An <b>Timespan definition</b> object</span>

- <TypeIcon type="string"/> **type**: One of the timespan definition types defined below
- <TypeIcon/> Additional fields depending on the value of type, see the respective timespan type documentation for more details.

</TreeView>

::: details Included Timespans

<RepoTimespans />

:::

## Supported Types

### Simple (`catharsis:simple`)

A timespan with a fixed start and end.
<Example>

A simple timespan that only matches nighttime.

<<< @/example_packs/TODO/assets/your_name_space/catharsis/timespans/is_day.json{json:line-numbers}
</Example>

<TreeView>
<span><TypeIcon type="object"/> Root <b>simple</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:simple`
- <TypeIcon type="int" /> **begin**: The starting point of the timespan in ticks, relative to the day.
- <TypeIcon type="int" /> **end**: The ending point of the timespan in ticks, relative to the day.

</TreeView>


### <Environment type="hypixel"/> Season (`catharsis:season`) <VersionTag>1.0.0-Beta.18</VersionTag>

A timespan about SkyBlock Seasons.

<TreeView>
<span><TypeIcon type="object"/> Root <b>season</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:season`
- <TypeIcon type="string" /><TypeIcon type="array" /> **season**/**seasons**: The SkyBlock Season, can be `EARLY_SPRING`, `SPRING`, `LATE_SPRING`, etc. 
    for `SPRING`, `SUMMER`, `AUTUMN`, `WINTER`.

</TreeView>
