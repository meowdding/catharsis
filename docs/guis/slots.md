---
title: Slot Conditions
lang: en-US
---

## Available Slot Conditions Types

#### **Is Tooltip Hidden** (`catharsis:is_tooltip_hidden`)

Match against a slot where the tooltip is hidden.

#### **Has Component** (`catharsis:has_component`)

Match against a slot that has a specific component.

<TreeView>
<span><TypeIcon type="object"/> Root <b>component</b> has_component condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:has_component`
- <TypeIcon type="string"/> **component**: The component ID to check for.

</TreeView>

#### **Name** (`catharsis:name`)

Match against a slot by its name using a regex.

<TreeView>
<span><TypeIcon type="object"/> Root <b>name</b> slot condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:name`
- <TypeIcon type="string"/> **mode**: Optional. Either `equals` or `regex`. Default: `regex`.
- <TypeIcon type="string"/> **name**: A string or regex to match against the slot's name depending on the mode.

</TreeView>

#### **Item** (`catharsis:item`)

Match against a slot by the item it contains.

<TreeView>
<span><TypeIcon type="object"/> Root <b>item</b> slot condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:item`
- <TypeIcon type="array"/> **items**: An array or a singular entry of item IDs to match against the slot's item.

</TreeView>

#### **ID** (`catharsis:id`)

Match against a slot by its SkyBlock ID.

<TreeView>
<span><TypeIcon type="object"/> Root <b>id</b> slot condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:id`
- <TypeIcon type="array"/> **ids**: An array or a singular entry of SkyBlock IDs to match against the slot's ID.

</TreeView>

#### **Slot Index** (`catharsis:slot`)

Match against a slot by its index in the GUI.

<TreeView>
<span><TypeIcon type="object"/> Root <b>slot</b> slot condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:slot`
- <TypeIcon/> **slot**: A range defining the slot index to match against. This can be a single number, an object with `min` and `max` properties or an array of such indices.

</TreeView>

### **Current Island** (`catharsis:islands`)

Match against the Player's current island.

<TreeView>
<span><TypeIcon type="object"/> Root <b>islands</b> slot condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:islands`
- <TypeIcon type="array"/> **islands**: A list or a singular entry of island ids to match against, names are equal to the value of `mode` in `/locraw` or the enum name in [SkyBlockIsland](https://github.com/SkyblockAPI/SkyblockAPI/blob/4.0/src/main/kotlin/tech/thatgravyboat/skyblockapi/api/location/SkyBlockIsland.kt).
</TreeView>

### **Skull Texture** (`catharsis:texture`)

Match against a skull by its texture, fails if no texture exists.

<TreeView>
<span><TypeIcon type="object"/> Root <b>texture</b> slot condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:texture`
- <TypeIcon type="array"/> **texture**: A list or a singular entry of textures as strings to match against.
</TreeView>


#### **All/And** (`catharsis:all`)

Check if a list of conditions are true, useful for chaining conditions.

<TreeView>
<span><TypeIcon type="object"/> Root <b>all</b> slot condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:all`
- <TypeIcon type="array"/> **conditions**: a list of conditions to check through.
</TreeView>

#### **Any/Or** (`catharsis:any`)

Check if any in list of conditions are true, useful for chaining conditions.

<TreeView>
<span><TypeIcon type="object"/> Root <b>any</b> slot condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:any`
- <TypeIcon type="array"/> **conditions**: a list of conditions to check through.
</TreeView>

### **Relative** (`catharsis:relative_slot`)

Allows you to apply other conditions based on relative slots.

<TreeView>
<span><TypeIcon type="object"/> Root <b>relative_slot</b> condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:relative_slot`
- <TypeIcon type="Int"/> **offset**: The relative offset for the slot.
- <TypeIcon type="object"/> **condition**: The child condition to check with the applied offset.
</TreeView>

### **Menu Border** (`catharsis:menu_border`)

Allows you to filter if a slot is on the border of a menu.

<TreeView>
<span><TypeIcon type="object"/> Root <b>menu_border</b> slot condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:menu_border`
- <TypeIcon type="Int"/> **allow_left**: (Optional) Whether the slot can be on the left border or not (defaults to true) 
- <TypeIcon type="Int"/> **allow_right**: (Optional) Whether the slot can be on the right border or not (defaults to true) 
- <TypeIcon type="Int"/> **allow_bottom**: (Optional) Whether the slot can be on the bottom border or not (defaults to true) 
- <TypeIcon type="Int"/> **allow_top**: (Optional) Whether the slot can be on the top border or not (defaults to true)
</TreeView>
