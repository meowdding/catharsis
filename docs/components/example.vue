<script setup>
import {useTemplateRef} from "vue";

const dialog = useTemplateRef("dialog")
const props = defineProps({
    title: {
        type: String,
        required: false
    }
})

</script>

<template>
    <dialog ref="dialog">
        <div class="box">
            <div class="header">
                <h3>{{ props.title ?? "Example" }}</h3>
                <button @click="dialog.close()">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M18 6 6 18"/>
                        <path d="m6 6 12 12"/>
                    </svg>
                </button>
            </div>
            <div class="content">
                <slot/>
            </div>
        </div>
    </dialog>
    <button @click="dialog.show()">Click to Show Example</button>
</template>

<style scoped>
button {
    cursor: pointer;

    font-weight: bold;
}

button:hover {
    color: var(--vp-c-indigo-1);
}

dialog {
    display: flex;
    justify-content: center;
    align-items: center;

    border: none;
    margin: 0;
    padding: 0;

    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    z-index: 1000000;

    background-color: rgba(25, 25, 25, 60%);

    &:not([open]) {
        display: none;
    }

    & > .box {
        min-width: 50vw;
        min-height: 50vh;

        max-width: 90vw;
        max-height: 90vh;

        background-color: var(--vp-c-bg);

        border-radius: 8px;

        & > .header {
            display: flex;
            justify-content: space-between;

            padding: 1rem;
            background-color: var(--vp-c-bg-alt);
            border-radius: 8px 8px 0 0;

            & > h3 {
                padding: 0;
                margin: 0;
            }

            & > button {
                display: flex;
                justify-content: center;
                align-items: center;

                width: 2rem;
                height: 2rem;

                border-radius: 4px;

                &:hover {
                    background-color: var(--vp-button-alt-hover-bg);
                }
            }
        }

        & > .content {
            width: 100%;
            height: 100%;
            padding: 0 1rem 1rem 1rem
        }
    }
}
</style>
