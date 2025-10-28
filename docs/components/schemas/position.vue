<script setup>
import TypeIcon from "../type-icon.vue";
import TreeView from "../tree-view.vue";

const props = defineProps({
    positionType: {
        type: String,
        required: true
    },
    customText: {
        type: Boolean,
        required: false
    }
})
</script>
<template>
    <TreeView collapsable>
        <template #title>
            <TypeIcon :type="positionType"/>
            <span>&nbsp;</span>
            <template v-if="!customText">
                <span v-if="positionType === 'float'">A position with float precision</span>
                <span v-else-if="positionType === 'int'">A position with int precision</span>
                <span v-else>A position with any precision</span>
            </template>
            <span v-else><slot/></span>
        </template>
        <ul>
            <li>
                <TypeIcon type="array"/>
                An array of three <span v-if="positionType === 'float'">floats</span><span v-else-if="positionType==='int'">ints</span><span v-else>numbers</span></li>
            <li>
                <TypeIcon type="string"/>
                A string of coordinates parts formatted as <code>x:y:z</code></li>
            <li>
                <TypeIcon type="object"/>
                An object with the coordinate values
                <ul>
                    <li>
                        <TypeIcon v-if="positionType !== 'float'" type="int"/>
                        <TypeIcon v-if="positionType !== 'int'" type="float"/>
                        &nbsp;<b>x</b>: The x part of the coordinate
                    </li>
                    <li>
                        <TypeIcon v-if="positionType !== 'float'" type="int"/>
                        <TypeIcon v-if="positionType !== 'int'" type="float"/>
                        &nbsp;<b>y</b>: The y part of the coordinate
                    </li>
                    <li>
                        <TypeIcon v-if="positionType !== 'float'" type="int"/>
                        <TypeIcon v-if="positionType !== 'int'" type="float"/>
                        &nbsp;<b>z</b>: The z part of the coordinate
                    </li>
                </ul>
            </li>
        </ul>
    </TreeView>
</template>
