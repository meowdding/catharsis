---
title: Fabric Overlays
lang: en-US
---

# Fabric Overlays Explanation

Pack overlays, (or also called Minipacks by a few), are a system provided by Fabric themselves.

Their docs are located [here](https://wiki.fabricmc.net/drafts:resourceconditions)

## Custom Overlay Conditions

### `catharsis:config`

<TreeView>
  <span><TypeIcon type="object"/> A <b>Fabric Condition Object</b> object</span>

- <TypeIcon type="string"/> **condition**: `catharsis:config`
- <TypeIcon type="string"/> **pack**: The ID of the pack containing the config.
- <TypeIcon type="string"/> **id**: The ID of the config option to check.
- <TypeIcon type="string"/> **value**: (Optional) The specific value to match (used for dropdowns).

</TreeView>

### `catharsis:version`

<TreeView>
  <span><TypeIcon type="object"/> A <b>Fabric Condition Object</b> object</span>

- <TypeIcon type="string"/> **condition**: `catharsis:version`
- <TypeIcon type="string"/> **type**: Either `MINECRAFT` or `PACK_FORMAT`
- <TypeIcon type="string"/> **minecraftPredicate**: __Required if MINECRAFT__, uses [Fabric's System](https://wiki.fabricmc.net/documentation:fabric_mod_json_spec?s[]=version)
- <TypeIcon type="object"/> **packFormatRange**: __Required if PACK_FORMAT__
    - <TypeIcon type="int"/> **min_inclusive**: The minimum pack format version.
    - <TypeIcon type="int"/> **max_inclusive**: The maximum pack format version.

</TreeView>
