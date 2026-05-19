---
title: Item
lang: en-US
---

# <Environment/> Conditional Item Properties

This page lists all custom conditional item properties that are added ontop of the vanilla ones.

#### **All/And** (`catharsis:all`)

Check if a list of conditions are true, useful for chaining conditions.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="array"/> **conditions**: a list of conditions to check through.
</TreeView>

#### **Any/Or** (`catharsis:any`)

Check if any in list of conditions are true, useful for chaining conditions.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="array"/> **conditions**: a list of conditions to check through.
</TreeView>

#### <Environment type="hypixel"/> **Data Type** (`catharsis:data_type`)

Check against a specific boolean data type.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="string"/> **data_type**: defines the data type to use. [all supported types](/miscellaneous/data_types)
</TreeView>

#### <Environment type="hypixel"/> **Has Data Type** (`catharsis:is_data_type_present`)

Returns `true` if the item has a specified data type.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="string"/> **data_type**: defines the data type to use. [all supported types](/miscellaneous/data_types)
</TreeView>

#### <Environment type="hypixel"/> **Has Gemstones** (`catharsis:has_gemstones`)

Returns `true` if the item has specified amount of gemstones.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="int"/> **amount**: the amount of gemstones to check for.
- <TypeIcon type="string"/> **slot**: Optional. The slot the gemstones can fit it, by default will act as universal slot.
- <TypeIcon type="string"/> **quality**: Optional. The quality of the gemstones, by default any quality is accepted.
</TreeView>


#### <Environment type="hypixel"/> **In Area** (`catharsis:in_area`)

Returns `true` if the player is in the specified area.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="string"/> **area**: The id of the area as defined [here](/models_visuals/block_replacements/areas)
</TreeView>

#### **Timespan** (`catharsis:timespan`)

Returns `true` if a certain [timespan](/miscellaneous/timespans) is true.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="string"/> **timespan**: The identifier of the timespan to check.
</TreeView>

#### **Is Hovered** (`catharsis:hovered`)

Returns `true` if the item is hovered.

#### <Environment type="hypixel"/> **Has Pet Skin** (`catharsis:has_pet_skin`)

Returns `true` if the pet has a skin applied.

#### <Environment type="hypixel"/> **Enchantment** (`catharsis:enchantment`)  <Version>1.0.0-beta.16</Version>

Returns `true` if the item has the specified enchantment at a matching level or within a matching level range.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="string"/> **enchant_name**: The identifier of the enchantment to check for.
- <TypeIcon type="object"/><TypeIcon type="int"/> **enchant_lvl**: The level requirement. Can be a single integer or an inclusive range object.
    - An int of the exact enchantment level required.
    - An object with the following fields specifying an inclusive range of enchantment levels required.
        - <TypeIcon type="int"/> **min_inclusive**: The minimum level required (inclusive).
        - <TypeIcon type="int"/> **max_inclusive**: The maximum level required (inclusive).
</TreeView>
