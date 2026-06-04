---
title: Inter Mod Communication (IMC)
lang: en-US
---

# <Environment /> Inter Mod Communication (IMC)

Catharsis provides Fabric entrypoints that other mods can invoke during initialization.

## Entrypoint Registry

Catharsis exposes two entrypoints under its IMC setup:

| Entrypoint Path          | Purpose                                                                        | Expected Value Type                                    |
|:-------------------------|:-------------------------------------------------------------------------------|:-------------------------------------------------------|
| `catharsis:imc/item_id`  | Applies a specific Catharsis ID to an `ItemStack` directly.                    | `java.util.function.BiConsumer<ItemStack, Identifier>` |
| `catharsis:imc/disabled` | Disables Catharsis retexturing and GUI behaviors for an item stack completely. | `java.util.function.BiConsumer<ItemStack, Boolean>`    |

## Registering Entrypoints

To link your mods compatibility handlers, register static method handles or consumer implementations inside your `fabric.mod.json` entrypoints block:

```json
"entrypoints": {
"catharsis:imc/item_id": [
"your.package.CatharsisSupport::id"
],
"catharsis:imc/disabled": [
"your.package.CatharsisSupport::disabled"
]
}
```

## Templates

::: details Kotlin Implementation

```kotlin
package your.package

import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.function.BiConsumer

object CatharsisSupport {

    private var idConsumer: BiConsumer<ItemStack, Identifier> = BiConsumer { _, _ -> }
    private var disabledConsumer: BiConsumer<ItemStack, Boolean> = BiConsumer { _, _ -> }

    @JvmStatic
    fun id(consumer: BiConsumer<ItemStack, Identifier>) {
        this.idConsumer = consumer
    }

    @JvmStatic
    fun disabled(consumer: BiConsumer<ItemStack, Boolean>) {
        this.disabledConsumer = consumer
    }

    fun ItemStack.disableCatharsisModifications() = apply {
        disabledConsumer.accept(this, true)
    }

    fun ItemStack.withCatharsisId(identifier: Identifier): ItemStack = apply {
        idConsumer.accept(this, identifier)
    }

    fun Item.withCatharsisId(identifier: Identifier): ItemStack = defaultInstance.apply {
        idConsumer.accept(this, identifier)
    }
}
```

:::

::: details Java Implementation

```java
package your.

package;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public class CatharsisSupport {

    private static BiConsumer<ItemStack, Identifier> idConsumer = (stack, id) -> {
    };
    private static BiConsumer<ItemStack, Boolean> disabledConsumer = (stack, disabled) -> {
    };

    public static void id(BiConsumer<ItemStack, Identifier> consumer) {
        idConsumer = consumer;
    }

    public static void disabled(BiConsumer<ItemStack, Boolean> consumer) {
        disabledConsumer = consumer;
    }

    public static ItemStack disableCatharsisModifications(ItemStack stack) {
        disabledConsumer.accept(stack, true);
        return stack;
    }

    public static ItemStack withCatharsisId(ItemStack stack, Identifier identifier) {
        idConsumer.accept(stack, identifier);
        return stack;
    }

    public static ItemStack withCatharsisId(Item item, Identifier identifier) {
        ItemStack stack = item.getDefaultInstance();
        idConsumer.accept(stack, identifier);
        return stack;
    }
}
```

:::
