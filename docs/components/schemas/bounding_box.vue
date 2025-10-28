<script setup>
import TypeIcon from "../type-icon.vue";
import Position from "./position.vue";
import TreeView from "../tree-view.vue";

const props = defineProps({
    positionType: {
        type: String,
        required: true
    },
    customText: {
        type: String,
        required: false
    }
})
</script>
<template>
    <TreeView collapsable>
        <template #title>
            <TypeIcon/>
            &nbsp;
            <span>A bounding box marking a region in 3d space</span>
        </template>
        <ul>
            <li><span><TypeIcon type="array"/> An array with two positions</span>
                <ul>
                    <li>
                        <Position position-type="int"/>
                    </li>
                </ul>
            </li>
            <li>
                <span><TypeIcon type="array"/> An array with 6 ints</span>
                <ul>
                    <li>
                        <span><TypeIcon type="int"/> The coordinates in order <code>minX</code>, <code>minY</code>, <code>minZ</code>, <code>maxX</code>, <code>maxY</code>, <code>maxZ</code></span>
                    </li>
                </ul>
            </li>
            <li>
                <span><TypeIcon type="object"/> An object with min and max</span>
                <ul>
                    <li>

                        <Position position-type="int" custom-text>
                            <b>min</b>: The min position
                        </Position>
                    </li>
                    <li>
                        <Position position-type="int" custom-text>
                            <b>max</b>: The max position
                        </Position>
                    </li>
                </ul>
            </li>
        </ul>
    </TreeView>
</template>
