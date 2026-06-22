import TreeView from "./tree-view.vue";
import TypeIcon from "./type-icon.vue";
import Position from "./schemas/position.vue";
import BoundingBox from "./schemas/bounding_box.vue";
import Island from "./schemas/island.vue";
import Example from "./example.vue";
import SoundEvent from "./schemas/sound_event.vue";
import RepoAreas from "./repo-areas.vue";
import RepoTimespans from "./repo-timespans.vue";
import VersionTag from "./version-tag.vue";
import Version from "./version.vue";
import Environment from "./environment.vue";
import ExamplePack from "./example-pack.vue";
import RepoGuis from "./repo-guis.vue";
import MiscItems from "./repo-items.vue";

export const CustomComponents: { [name: string]: any } = {
    "TreeView": TreeView,
    "TypeIcon": TypeIcon,
    "Position": Position,
    "BoundingBox": BoundingBox,
    "Island": Island,
    "Example": Example,
    "SoundEvent": SoundEvent,
    "VersionTag": VersionTag,
    "Environment": Environment,
    "ExamplePack": ExamplePack,
    "RepoAreas": RepoAreas,
    "RepoTimespans": RepoTimespans,
    "RepoGuis": RepoGuis,
    "MiscItems": MiscItems,
    "Version": Version,
}
