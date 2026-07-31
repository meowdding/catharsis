---
title: GUI Modifiers
lang: en-US
---

# <Environment/> GUI Modifiers

GUI Modifiers allow Texture Packs to modify containers based on a [GUI Definition](./definitions.md).

You can define a gui modifiers in `assets/<namespace>/catharsis/gui_modifiers/<id>.json`.

## Json format

<TreeView>
<span><TypeIcon type="object"/> Root object</span>
<ul>

- <TypeIcon type="object"/> **target**: [A GUI Definition Target](#gui-definition-target)
- <TypeIcon type="boolean"/> **overrideLabels**: (Optional) Disables rendering the Container Title, defaults to `false`.
- <TypeIcon type="boolean"/> **overrideBackground**: (Optional) Disables rendering the Container Background Texture, defaults to `false`.

<Version type="1.0.0-beta.18">

- <TypeIcon type="boolean"/> **disableItemList**: (Optional) Disables the rendering of item list mods, defaults to `false`.

</Version>

- <TypeIcon type="object"/> **bounds**: (Optional) Determines if you clicked outside the container, uses the default container bounds if not specified.
    - <TypeIcon type="int"/> **width**: The width, should be 100 when using the default size.
    - <TypeIcon type="int"/> **height**: The height, should be 100 when using the default size.

<Version type="1.0.0-beta.18">

- <TypeIcon type="array"/> **itemListExclusionZones** (Optional) Specific exclusion zones for item list mods to exclude.
    - <TypeIcon type="object"/> An Exclusion Zone object.
        - <TypeIcon type="int"/> **x**: The x coordinate where the exclusion zone starts.
        - <TypeIcon type="int"/> **y**: The y coordinate where the exclusion zone starts.
        - <TypeIcon type="int"/> **width**: The width of the exclusion zone.
        - <TypeIcon type="int"/> **height**: The height of the exclusion zone.

</Version>

- <TypeIcon type="object"/> **slots**: (Optional) A map of slots to modify.
    - <TypeIcon type="string"/> **&lt;key&gt;**: The id of slot you want to modify
      - <TypeIcon type="object"/> [A slot modifier](#slot-modifier).
- <TypeIcon type="array"/> **elements**: (Optional) A list of [GUI elements](#gui-elements).
- <TypeIcon type="array"/> **widgets**: (Optional) A list of [GUI widgets](#gui-widgets).

<Version type="1.0.0-beta.21">

- <TypeIcon type="array"/> **hiddenModElements**: (Optional) A list of specific Mod UI elements to hide (supported by compatible mods).

</Version>

</ul>
</TreeView>

## GUI Definition Target

### **Existing GUI Definition** (`catharsis:definition`)

Target to an existing GUI Definition

<TreeView>
<span><TypeIcon type="object"/> Root <b>definition</b> gui modifier target object</span>

- <TypeIcon type="string"/> **type**: `catharsis:definition`
- <TypeIcon type="string"/> **definition**: An existing GUI definition's identifier.

</TreeView>

## Slot Modifier

Modify a slot by moving it, disabling it, etc.

<TreeView>
<span><TypeIcon type="object"/> Root <b>Slot Modifier</b> map value</span>

- <TypeIcon type="boolean"/> **hidden**: (Optional) Hides the slot, defaults to `false`.
- <TypeIcon type="boolean"/> **highlightable**: (Optional) Hides the hover highlight, defaults to `true`.
- <TypeIcon type="object"/> **position**: (Optional) Moves the slot to a new x,y.
    - <TypeIcon type="int"/> **x**: The new x coordinate of the slot.
    - <TypeIcon type="int"/> **y**: The new y coordinate of the slot.
- <TypeIcon type="boolean"/> **clickable**: (Optional) Removes the ability to click a slot, defaults to `true`.

</TreeView>

## GUI Elements

### **Player** (`catharsis:player`)

Render the Player itself on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <b>GUI element</b> object</span>

<ul>

- <TypeIcon type="string"/> **type**: `catharsis:player`

<Version type="1.0.0-beta.20">

  - <TypeIcon type="object"/> **condition**: (Optional) A [GUI element condition](#gui-element-conditions) to determine if this element should render.
</Version>

- <TypeIcon type="object"/> **x**: The x coordinate & alignment of the player. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The x coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the player, can be `START`, `CENTER` or `END`.
- <TypeIcon type="object"/> **y**: The y coordinate & alignment of the player. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The y coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the player, can be `START`, `CENTER` or `END`.
- <TypeIcon type="int"/> **width**: The width of the player.
- <TypeIcon type="int"/> **height**: The height of the player.
- <TypeIcon type="string"/> **rotation**: (Optional) The players rotation, will follow the mouse if undefined. **Can only be one of the entries below.**
    - <TypeIcon type="array"/> A list of 4 floats: first, second, and the third component of the imaginary part, and the real part.
    - <TypeIcon type="object"/> An AxisAngle definition.
        - <TypeIcon type="float"/> **angle**: The angle in radians.
        - <TypeIcon type="array"/> **axis**: A list of 4 floats: The value of x, y, and z.

</ul>
</TreeView>

<Version type="1.0.0-beta.21">

### **Entity** (`catharsis:entity`)

Render a custom Entity on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <b>GUI element</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:entity`
- <TypeIcon type="object"/> **condition**: (Optional) A [GUI element condition](#gui-element-conditions) to determine if this element should render.
- <TypeIcon type="object"/> **x**: The x coordinate & alignment of the entity. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The x coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the entity, can be `START`, `CENTER` or `END`.
- <TypeIcon type="object"/> **y**: The y coordinate & alignment of the entity. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The y coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the entity, can be `START`, `CENTER` or `END`.
- <TypeIcon type="int"/> **width**: The width of the entity.
- <TypeIcon type="int"/> **height**: The height of the entity.
- <TypeIcon type="string"/> **rotation**: (Optional) The entity rotation, will follow the mouse if undefined, cannot follow the mouse if it's a boat or similar. 
    **Can only be one of the entries below.**
    - <TypeIcon type="array"/> A list of 4 floats: first, second, and the third component of the imaginary part, and the real part.
    - <TypeIcon type="object"/> An AxisAngle definition.
        - <TypeIcon type="float"/> **angle**: The angle in radians.
        - <TypeIcon type="array"/> **axis**: A list of 4 floats: The value of x, y, and z.
- <TypeIcon type="string"/> **entityType**: The vanilla entity registry name to target (e.g., `minecraft:zombie`).
- <TypeIcon type="string"/><TypeIcon type="object"/> **tag**: (Optional) Custom entity data.
    - <TypeIcon type="string"/> Snbt inside a string, can be copied from [McStacker](https://mcstacker.net/?cmd=summon) directly.
    - <TypeIcon type="object"/> A JSON Object of the data.

</TreeView>
</Version>

### **Sprite** (`catharsis:sprite`)

Render a sprite on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <b>GUI element</b> object</span>

<ul>

- <TypeIcon type="string"/> **type**: `catharsis:sprite`

<Version type="1.0.0-beta.20">

  - <TypeIcon type="object"/> **condition**: (Optional) A [GUI element condition](#gui-element-conditions) to determine if this element should render.
</Version>

- <TypeIcon type="string"/> **sprite**: The Identifier of the sprite to render.
- <TypeIcon type="string"/> **layer**: (Optional) The layer to render the sprite on, defaults to `BACKGROUND`. Can be `BACKGROUND` & `FOREGROUND`.
- <TypeIcon type="object"/> **x**: (Optional) The x coordinate & alignment of the sprite. Defaults to `0, START`. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The x coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the sprite, can be `START`, `CENTER` or `END`.
- <TypeIcon type="object"/> **y**: (Optional) The y coordinate & alignment of the sprite. Defaults to `0, START`. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The y coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the sprite, can be `START`, `CENTER` or `END`.
- <TypeIcon type="int"/> **width**: (Optional) The width of the sprite, defaults to the container width.
- <TypeIcon type="int"/> **height**: (Optional) The height of the sprite, defaults to the container height.

</ul>
</TreeView>

### **Text** (`catharsis:text`)

Render text on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <b>GUI element</b> object</span>

<ul>

- <TypeIcon type="string"/> **type**: `catharsis:text`

<Version type="1.0.0-beta.20">

  - <TypeIcon type="object"/> **condition**: (Optional) A [GUI element condition](#gui-element-conditions) to determine if this element should render.
</Version>

- <TypeIcon type="object"/> **text**: [A component in JSON format.](https://minecraft.wiki/w/Text_component_format)
- <TypeIcon type="int"/> **color**: (Optional) The color to render the base string with, defaults to [`-12566464`](https://www.hexcolortool.com/#6844f8).
- <TypeIcon type="object"/> **x**: The x coordinate & alignment of the text. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The x coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the text, can be `START`, `CENTER` or `END`.
- <TypeIcon type="object"/> **y**: The y coordinate & alignment of the text. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The y coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the text, can be `START`, `CENTER` or `END`.
- <TypeIcon type="float"/> **alignment**: (Optional) The text alignment based on the coordinates above, defaults to `0`. 0 is left, 0.5 is center, 1 is right.

</ul>
</TreeView>

## GUI Widget

### Button (`catharsis:button`)

Render a button on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <b>GUI widget</b> object</span>

<ul>

- <TypeIcon type="string"/> **normal**: The Identifier of the texture to render.
- <TypeIcon type="string"/> **hovered**: (Optional) The Identifier of the texture to render when the button is hovered, defaults to the normal texture.

<Version type="1.0.0-beta.20">

- <TypeIcon type="object"/> **condition**: (Optional) A [GUI element condition](#gui-element-conditions) to determine if this widget should render and be interactable.
</Version>

<Version type="1.0.0-beta.21">

- <TypeIcon type="object"/> **interaction**: (Optional) The interaction when clicked. **Can only be one of the entries below.**
    - <TypeIcon type="object"/> A single widget interaction object (Defaults to Left&Right Click), view [here](#gui-widget-interaction).
    - <TypeIcon type="object"/> A map of mouse buttons (`left`, `right`, `middle`) to widget interaction objects.

</Version>

<VersionNot type="1.0.0-beta.21">

- <TypeIcon type="object"/> **interaction**: The interaction when clicked, view [here](#gui-widget-interaction).

</VersionNot>

<Version type="1.0.0-beta.20">

- <TypeIcon type="object"/><TypeIcon type="list"/> **tooltip**: (Optional) The tooltip when hovered, view [here](#gui-widget-tooltip). Defaults to no tooltip.
    - <TypeIcon type="object"/> A singular tooltip object.
    - <TypeIcon type="list"/> A list of tooltip objects
</Version>

<VersionNot type="1.0.0-beta.20">

- <TypeIcon type="object"/> **tooltip**: (Optional) The tooltip when hovered, view [here](#gui-widget-tooltip). Defaults to no tooltip.
</VersionNot>

- <TypeIcon type="object"/> **x**: The x coordinate & alignment of the button. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The x coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the button, can be `START`, `CENTER` or `END`.
- <TypeIcon type="object"/> **y**: The y coordinate & alignment of the button. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The y coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the button, can be `START`, `CENTER` or `END`.
- <TypeIcon type="int"/> **width**: The width of the button.
- <TypeIcon type="int"/> **height**: The height of the button.

<Version type=1.0.0-beta.21>

- <TypeIcon type="string"/> **shape**: (Optional) The shape of the widget used for hovering & clicking, can be `RECTANGLE`, `ELLIPSE`, `DIAMOND`, defaults to `RECTANGLE`.

</Version>

</ul>

</TreeView>


<Version type="1.0.0-beta.21">

### Item Stack (`catharsis:item_stack`)
Render an item stack on the screen. 
It automatically displays the item's tooltip when hovered, which can be overridden with custom tooltips.

<TreeView>
<span><TypeIcon type="object"/> Root <b>GUI widget</b> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:item_stack`
- <TypeIcon type="object"/> **condition**: (Optional) A [GUI element condition](#gui-element-conditions) to determine if this element should render.
- <TypeIcon type="object"/> **item**: An [Item Stack Provider](#item-stack-providers) to determine what item to render.
- <TypeIcon type="string"/> **layer**: (Optional) The layer to render the item on, defaults to `FOREGROUND`. Can be `BACKGROUND` & `FOREGROUND`.
- <TypeIcon type="object"/> **interaction**: (Optional) The interaction when clicked. **Can only be one of the entries below.**
    - <TypeIcon type="object"/> A single widget interaction object (Defaults to Left&Right Click), view [here](#gui-widget-interaction).
    - <TypeIcon type="object"/> A map of mouse buttons (`left`, `right`, `middle`) to widget interaction objects.
- <TypeIcon type="object"/><TypeIcon type="list"/> **tooltip**: (Optional) Defining this overrides the Item's default tooltips, view [here](#gui-widget-tooltip).
- <TypeIcon type="object"/> **x**: The x coordinate & alignment of the item. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The x coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the item, can be `START`, `CENTER` or `END`.
- <TypeIcon type="object"/> **y**: The y coordinate & alignment of the item. **Can only be one of the entries below.**
    - <TypeIcon type="int"/> The y coordinate based from the `START`.
    - <TypeIcon type="object"/> Coordinate & Alignment object.
        - <TypeIcon type="int"/> **offset**: The offset from the screen bounds (main container size).
        - <TypeIcon type="string"/> **alignment**: The alignment of the item, can be `START`, `CENTER` or `END`.
- <TypeIcon type="int"/> **width**: (Optional) The width of the item, defaults to 16. 3d items will become pixelated when using a width above 16.
- <TypeIcon type="int"/> **height**: (Optional) The height of the item, defaults to 16. 3d items will become pixelated when using a height above 16.
</TreeView>

#### Item Stack Providers

Item Stack Providers allow you to define the source of an `ItemStack`.

##### **JSON** (`catharsis:json`)
Loads an item stack from Minecraft Item NBT JSON.

<TreeView>
<span><TypeIcon type="object"/> Root <b>json</b> item stack provider object</span>

- <TypeIcon type="string"/> **type**: `catharsis:json`
- <TypeIcon type="object"/> **stack**: Standard Minecraft ItemStack JSON definition.
</TreeView>

#### **SkyBlock ID** (`catharsis:sbid`)
Loads an item stack using a SkyBlock ID.

Id Format: `<prefix>:<id>` or `<prefix>:<id>:<suffix>`

| Prefix        | Id                      | Suffix        |
|---------------|-------------------------|---------------|
| `item`        | The normal id           | No Suffix     |
| `attribute`   | The Attribute id        | No Suffix     |
| `enchantment` | The enchant id          | Enchant Level |
| `pet`         | The Pet Id from petInfo | Pet Rarity    |
| `potion`      | The Potion Id           | Potion Level  |

<TreeView>
<span><TypeIcon type="object"/> Root <b>sbid</b> item stack provider object</span>

- <TypeIcon type="string"/> **type**: `catharsis:sbid`
- <TypeIcon type="string"/> **id**: The SkyBlock Id as formatted above.
</TreeView>
</Version>


## GUI Widget Interaction

### **Link** (`catharsis:link`)

Open a link in the default browser.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget interaction</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:link`
- <TypeIcon type="string"/> **url**: A link to the Site.

</TreeView>

### **Slot Clicking** (`catharsis:slot`)

Click a slot on the screen.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget interaction</bold> object</span>
<ul>

- <TypeIcon type="string"/> **type**: `catharsis:slot`
- <TypeIcon type="int"/> **slot**: The slot to click.

<Version type=1.0.0-beta.21>

- <TypeIcon type="boolean"/> **alwaysMiddleClick**: (Optional) Middle clicks the slot to avoid picking it up, clicks will pass through with shift when disabled. Defaults to `true`.

</Version>
</ul>

</TreeView>

<Version type=1.0.0-beta.20>

### **Gui Id Slot Clicking** (`catharsis:slot_id`)

Click a slot on the screen based the Gui Id.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget interaction</bold> object</span>
<ul>

- <TypeIcon type="string"/> **type**: `catharsis:slot_id`
- <TypeIcon type="string"/> **slot**: The identifier of the slot.

<Version type=1.0.0-beta.21>

- <TypeIcon type="boolean"/> **alwaysMiddleClick**: (Optional) Middle clicks the slot to avoid picking it up, clicks will pass through with shift when disabled. Defaults to `true`.

</Version>
</ul>

</TreeView>
</Version>

<Version type=1.0.0-beta.19>

### **Sending a command** (`catharsis:command`)

Send a command in the chat.

::: warning
The commands are based on a [whitelist](https://github.com/meowdding/catharsis/blob/main/repo/commands.json), if you need more, ask in the Discord. 
:::

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget interaction</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:command`
- <TypeIcon type="string"/> **command**: The command to send.

</TreeView>

</Version>

## GUI Widget Tooltip

### **Text** (`catharsis:text`)

Renders a custom defined text.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget tooltip</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:text`
- <TypeIcon type="object"/><TypeIcon type="array"/> **text**: A list of or a single [component in JSON format.](https://minecraft.wiki/w/Text_component_format)

</TreeView>

### **Slot Index** (`catharsis:slot`)

Steals the tooltip from an existing slot.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget tooltip</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:slot`
- <TypeIcon type="int"/> **slot**: The slot to steal the tooltip from.

</TreeView>

<Version type=1.0.0-beta.21>

### **Gui Id Slot** (`catharsis:slot_id`)

Steals the tooltip from an existing slot using a slot id, uses the first item found it multiple.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget tooltip</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:slot_id`
- <TypeIcon type="string"/> **slot**: The identifier of the slot.

</TreeView>
</Version>

### **Widget Interaction** (`catharsis:interaction`)

Steals the tooltip from the defined widget interaction.
Only works if the interaction is [`catharsis:slot`](#slot-clicking-catharsis-slot).

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget tooltip</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:interaction`

</TreeView>

<Version type="1.0.0-beta.20">

### **SkyBlock Id** (`catharsis:id`)

Steals the tooltip from an SkyBlock Item.

Id Format: `<prefix>:<id>` or `<prefix>:<id>:<suffix>`

| Prefix        | Id                      | Suffix        |
|---------------|-------------------------|---------------|
| `item`        | The normal id           | No Suffix     |
| `attribute`   | The Attribute id        | No Suffix     |
| `enchantment` | The enchant id          | Enchant Level |
| `pet`         | The Pet Id from petInfo | Pet Rarity    |
| `potion`      | The Potion Id           | Potion Level  |

<TreeView>
<span><TypeIcon type="object"/> Root <bold>widget tooltip</bold> object</span>

- <TypeIcon type="string"/> **type**: `catharsis:id`
- <TypeIcon type="int"/> **id**: The SkyBlock Id as formatted above.
- <TypeIcon type="boolean"/> **withName**: (Optional) If the name should be included, defaults to `true`.
- <TypeIcon type="boolean"/> **withLore**: (Optional) If the lore should be included, defaults to `true`.

</TreeView>
</Version>

<Version type="1.0.0-beta.20">

## GUI Element & Widget Conditions

GUI Element Conditions allow you to conditionally render GUI elements and widgets based on different conditions.

### **Has Slot** (`catharsis:has_slot`)
Checks if a specific slot definition exists in the current GUI.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>has_slot</bold> element condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:has_slot`
- <TypeIcon type="string"/> **slot**: The ID of the slot definition to check for.

</TreeView>

### **All/And** (`catharsis:and`)
Checks if all listed conditions are true.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>and</bold> element condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:and`
- <TypeIcon type="array"/> **conditions**: A list of GUI element conditions to check.

</TreeView>

### **Any/Or** (`catharsis:or`)
Checks if any of the listed conditions are true.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>or</bold> element condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:or`
- <TypeIcon type="array"/> **conditions**: A list of GUI element conditions to check.

</TreeView>

### **Exclusive Or** (`catharsis:xor`)
Checks if exactly one of the listed conditions is true.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>xor</bold> element condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:xor`
- <TypeIcon type="array"/> **conditions**: A list of GUI element conditions to check.

</TreeView>

### **Not** (`catharsis:not`)
Inverts the result of a condition.

<TreeView>
<span><TypeIcon type="object"/> Root <bold>not</bold> element condition object</span>

- <TypeIcon type="string"/> **type**: `catharsis:not`
- <TypeIcon type="object"/> **condition**: The GUI element condition to invert.

</TreeView>
</Version>
