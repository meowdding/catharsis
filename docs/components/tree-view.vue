<script setup>
const props = defineProps({
    collapsable: {
        type: Boolean,
        required: false
    }
})
</script>

<template>
    <details v-if="props.collapsable" class="tree-view">
        <summary><slot name="title"/><span class="collapsable-button"/></summary>
        <slot/>
    </details>
    <div v-else class="tree-view">
        <slot/>
    </div>
</template>

<style>
.tree-view {
    display: flex;
    flex-direction: column;
    gap: 4px;

    ul {
        margin: 0;
        padding: 0;
        list-style: none;
    }

    ul ul {
        margin-top: 0;
    }

    li, li + li {
        margin-top: 0;
        list-style: none;
        padding-left: 16px;
        margin-left: 8px;
        position: relative;

        &:not(:last-child) {
            border-left: 1px solid var(--vp-c-text-1);
        }

        &:last-child {
            border-left: 1px solid transparent;
        }
    }

    li:before {
        content: '';
        display: block;
        position: absolute;
        width: 12px;
        height: 12px;
        border-bottom: 1px solid var(--vp-c-text-1);
        left: -1px;
    }

    li:last-child:before {
        border-left: 1px solid var(--vp-c-text-1);
    }

    details {
        & > summary {
            position: relative;
            margin: 0;

            &::marker {
                content: "";
            }

            .collapsable-button:before {
                content: " [Show]";
                cursor: pointer;

                font-weight: bold;
            }

            .collapsable-button:hover:before {
                color: var(--vp-c-indigo-1);
            }
        }

        &[open] > summary .collapsable-button:before {
            content: " [Hide]";
        }
    }
}
</style>
