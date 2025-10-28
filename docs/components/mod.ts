import TreeView from "./tree-view.vue";
import TypeIcon from "./type-icon.vue";
import Position from "./schemas/position.vue";
import BoundingBox from "./schemas/bounding_box.vue";
import Island from "./schemas/island.vue";

export const CustomComponents: { [name: string]: any } = {
    "TreeView": TreeView,
    "TypeIcon": TypeIcon,
    "Position": Position,
    "BoundingBox": BoundingBox,
    "Island": Island
}
