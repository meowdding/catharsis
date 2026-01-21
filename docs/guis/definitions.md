---
title: Gui Definitions
lang: en-US
---

# Gui Definitions

In Catharsis we support a way to define definitions and ids for specific guis and their slots.
This allows you to create dynamic guis that can be referenced in other parts of Catharsis or in Item Models.

You can define a gui definition in `assets/<namespace>/catharsis/guis/<id>.json`.

## Json format

<TreeView>
<span><TypeIcon type="object"/> Root object</span>

- <TypeIcon type="int"/> **priority**: The priority of the gui definition. Use it for generic guis that will apply to all.
- <TypeIcon type="object"/> **target**: The gui definition target.
- <TypeIcon type="array"/> **layout**: The slots to define in this gui.
    - <TypeIcon type="object"/> Slot Definition Object
        - <TypeIcon type="string"/> **id**: The id of the slot to define, this will be used to reference in item models.
        - <TypeIcon type="object"/> **target**: The slot condition to match against.

</TreeView>

### Gui Definition Target

<TreeView>
<span><TypeIcon type="object"/> A <b>Gui Definition Target</b> object</span>

- <TypeIcon type="string"/> **type**: One of the types below.
- <TypeIcon/> Additional fields depending on the value of type, see the respective target type documentation for more details.

</TreeView>

## Available Gui Definition Target Types

#### **Title** (`catharsis:title`)

Match against a specific gui title.

<TreeView>
<span><TypeIcon type="object"/> Root <b>title</b> gui definition target object</span>

- <TypeIcon type="string"/> **type**: `catharsis:title`
- <TypeIcon type="string"/> **title**: A regex pattern to match the gui title against.

</TreeView>

### **Islands** (`catharsis:islands`)

Match against the Player's current island.

<TreeView>
<span><TypeIcon type="object"/> Root <b>islands</b> gui definition target object</span>
- <TypeIcon type="string"/> **type**: `catharsis:islands`
- <TypeIcon type="array"/> **islands**: A list of island ids to match against, names are equal to the value of `mode` in `/locraw` or the enum name in [SkyBlockIsland](https://github.com/SkyblockAPI/SkyblockAPI/blob/4.0/src/main/kotlin/tech/thatgravyboat/skyblockapi/api/location/SkyBlockIsland.kt).
</TreeView>

#### **Slot** (`catharsis:slot`)

Match against a gui with a specific slot condition.

<TreeView>
<span><TypeIcon type="object"/> Root <b>slot</b> gui definition target object</span>

- <TypeIcon type="string"/> **type**: `catharsis:slot`
- <TypeIcon type="integer"/> **index**: The slot index to match against.
- <TypeIcon type="object"/> **condition**: The slot condition to match against.

</TreeView>

#### **All/And** (`catharsis:all`)

Check if a list of conditions are true, useful for chaining conditions.

<TreeView>
<span><TypeIcon type="object"/> Root <b>all</b> gui definition target object</span>

- <TypeIcon type="string"/> **type**: `catharsis:all`
- <TypeIcon type="array"/> **conditions**: a list of conditions to check through.
</TreeView>

#### **Any/Or** (`catharsis:any`)

Check if any in list of conditions are true, useful for chaining conditions.

<TreeView>
<span><TypeIcon type="object"/> Root <b>any</b> gui definition target object</span>

- <TypeIcon type="string"/> **type**: `catharsis:any`
- <TypeIcon type="array"/> **conditions**: a list of conditions to check through.
</TreeView>
