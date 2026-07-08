---
title: Text Replacements
lang: en-US
---

# <Environment/> Text Replacements

Text replacements allow you to modify text components in the game, such as item names, lore and nametags.

## Definition

Definitions should be placed in: `assets/<namespace>/catharsis/text_replacements/<type>/<id>.json`.

Existing Types:

| Type    | Desc                   |
|---------|------------------------|
| item    | For Item Name and Lore |
| nametag | For entity nametags    |

<TreeView>
<span><TypeIcon type="object"/> A <b>text replacement</b> object</span>

- <TypeIcon type="int"/> **priority**: Optional. Higher numbers are processed later. Defaults to `0`.
- <TypeIcon type="object"/> **replacer**: The [replacer definition](#replacer-types) to use.

</TreeView>

## Replacer Types

### Regex (`catharsis:regex`)

Replaces text using regular expressions.

<TreeView>
<span><TypeIcon type="object"/> Root <b>regex</b> replacer object</span>

- <TypeIcon type="string"/> **type**: `catharsis:regex`
- <TypeIcon type="string"/> **regex**: The regular expression pattern to match.
- <TypeIcon type="string"/> **replacement**: The replacement text. Supports regex groups (e.g. `$1`).
- <TypeIcon type="boolean"/> **propagate**: Optional. If `false`, stops further replacements in the chain if this one matches. Defaults to `true`.

</TreeView>

### Composite (`catharsis:composite`)

Groups multiple replacers together to run them in sequence.

<TreeView>
<span><TypeIcon type="object"/> Root <b>composite</b> replacer object</span>

- <TypeIcon type="string"/> **type**: `catharsis:composite`
- <TypeIcon type="array"/> **replacers**: A list of [replacer definitions](#replacer-types).
- <TypeIcon type="boolean"/> **propagate**: Optional. If `false`, stops the chain if a sub-replacer triggers a break. Defaults to `true`.

</TreeView>
