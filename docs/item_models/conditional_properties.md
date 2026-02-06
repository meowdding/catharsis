---
title: Item
lang: en-US
---

# Conditional Item Properties

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

#### **Data Type** (`catharsis:datatype`)

Check against a specific boolean data type.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="string"/> **data_type**: defines the data type to use. [all supported types](../miscellaneous/data_types)
</TreeView>

#### **Has Data Type** (`catharsis:has_data_type`)

Returns `true` if the item has a specified data type.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="string"/> **data_type**: defines the data type to use. [all supported types](../miscellaneous/data_types)
</TreeView>

#### **Has Gemstones** (`catharsis:has_gemstones`)

Returns `true` if the item has specified amount of gemstones.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="int"/> **amount**: the amount of gemstones to check for.
- <TypeIcon type="string"/> **slot**: Optional. The slot the gemstones can fit it, by default will act as universal slot.
- <TypeIcon type="string"/> **quality**: Optional. The quality of the gemstones, by default any quality is accepted.
</TreeView>


#### **In Area** (`catharsis:in_area`)

Returns `true` if the player is in the specified area.

<TreeView>
<span>additional fields:</span>

- <TypeIcon type="string"/> **area**: The id of the area as defined [here](/block_replacements/areas)
</TreeView>

#### **Is Hovered** (`catharsis:hovered`)

Returns `true` if the item is hovered.

#### **Has Pet Skin** (`catharsis:has_pet_skin`)

Returns `true` if the pet has a skin applied.
