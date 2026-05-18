<script setup>
const props = defineProps({
    type: {
        type: String,
        required: false
    }
})
let hover
let icon
switch (props.type) {
    case "hypixel":
        hover = "This Feature only works on Hypixel";
        icon = "hypixel"
        break;
    case "minecraft":
        hover = "This Feature only works in vanilla Minecraft";
        icon = "minecraft"
        break;
    case "both":
    default:
        hover = "This Feature works in both vanilla & on Hypixel";
        icon = "hypixel_minecraft"
        break;
}
</script>

<template>
    <span class="platform-icon" :data-type="icon" :title="hover"></span>
</template>

<style scoped>
.platform-icon {
    display: inline-block;
    width: 1em;
    height: 1em;
    vertical-align: middle;

    background-size: contain;
    background-repeat: no-repeat;
    background-position: center;

    background-image: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAABDVJREFUWIWVl1Fy5EYMQx9oHTDJEbPlH9/RxH4QbGk2m8RWlcuSZqabBAGQrb9//LBsDCCYm7mU/86d9PiSQBSSwUL1/MXXr8vdOIvM5hOMEL1L2rhA3i3yXTU2lKANZXDpW2FctpHNH3/+hTQbI5A09wUk05ImNM3f+XjS/9b18fGxAcD8NUjYE4RliskaGqMgE4w8NemBiDrl+d41JSBRtBNEIc9G5obUG4GMKerBF1tQz29/MYCm2f33Eo1bU1yHcFa4YmzxZtNl5Ao2oBYozNHXArnc8+N2uC5TS7LecoDVzFZCvXx1KBsUEqjQqOMLaFx26mnPAp18auvMCNGaCtVuLEjGy5kjFYCuO4j/iONGAB/0WFluSQK/gOHqEnIC6TBFkev9ySAh868ludoeY+nG50s6SwSes8C+t4QGc/AEVGzgQpqwsLBC2N/EUE3T7ltaNrbp6LMxPSTB9kh27zHDYaOs4R5JOxxykutdP1Esdy56PmibeshOhq4eTsi0FJYPVyrvUTaKGUmNXVNUCboojdktjSu8AnF1B2IP04msbpLd9sMxZ41xWftI08iFywliEqljYjpybcA9/IgKBtLb31+ZuyTyMpxks07kuzeQhaXDHkKQl8tR3TWZzIs4ziGfFpzKf4mKMeEOQq9ShZRr7yOAAXd5Ngu6NRw4plLL6IHaMlKhdcNmUOi3KdMvHt16IDbUBcXW9eory8eruVmvnjpNBoHxafjHHz4xlS4W0dbN8t1Qnvf3CtoKHou/7I4Q/Og2p/GjTqYpwda33agrqlj4Vz37LORmvPLZ1CZpWTGi1WxxyCTfnFXgLTwS69lJ7vh/vfSEM9+oqUR/2vjWMs8PK+7ofFjdA/JNIvv5JhOTj0/IGmRq5HhKKJ8EFqVTgi6u42Z9S0c21igYKWXQQWPlpbWJKGXQHP4sRUg31SJdD0dQcxF75dmSY29GFL79KKRSJE+tEmcuGLTHI2oJXaY8vYMzXqTXDAd4mUZsnyLqAfhMnwv5ZO8nFJgQ5WysbUbLgfQ067CBC/dzEn/I2kcMZ0o2aa8aIR7H9FNky9GZB7pCbt2zAn32uKZr/RrCJuRxvng5yWgXenYJ2XSJ6tvGO85aLRxysi1bU7ZrOPibAG4Q0pwSVwg1jScy0wR4u3NCO0pYBXTaQp/QL9OPJvP7aymyzjBqGq9omWr4PGeEIRcPapymlJ70qR0Bi4vMgf93mVHLGa8SxJ4lJrDZfP3sni0GCTSnrePuMtc/DoRfCOQUxqJ26nks07kvwqMVt9eqOXy69M0AEsV4uYKMCrVfMC9xsn127VIOkqH1Nc5bvL+/Z87jZmqOfdrzYGmyKfFGQc07UaigVKiKYk7L2mcNQUpvs9YbVAp1IZ2BqV9UPxgOeysGUzmQ1jGjPazWPazlgzrPw/wca3a2fJvf/gQyBVjwO0x8sQAAAABJRU5ErkJggg==");

    &[data-type="hypixel"] {
        background-image: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAwZJREFUWAnlVk1oE0EUftVas2lLIBE1bfwPaKwFQ/SiVYiC4kEUvHiQ3uulIhX0onjPwYMY9OpVD6WIeJCCf1Da4m8sgmDVtMVqY0J1o9W07jeb2Uz2d9YWPDiQzMyb9973zXtvZpbof28NSxmAjuTOBTt/uafPHHEa7QycZGYA0THWHl7fQsF4DzNX32ZpZvQ3Hb/x3skdk0sR4MAPrm6mwosKjT3+SBdy84ZjERzAZ08OUUvTMn1dCRt6dgNPAnAO4ObEaWbfnCC6O3BGG9ebYrfBONWBD2rgYpR8EwD46P0Ms/s+ds3O3pAhKpFU1pjHokGiojF1HNRvQ1Dj4HbAG8NNRFO1FAhmLPQAv1kMeO4edo4EsJg62EfpcoG+zc1T9tFRUt99gpjadkSIcp/ZmP8l9q7jQ199tVKsNsgdfsjjhvBK6ukaoOCmNUwxFNLCu0TNkQD3DxK3Kq0GCS536hH+7e3yBD0J2AGVSqpF3Nii3zUA95MO1xqwoEgIOg+vp9DW5RKauoo0AaV1BVHhJ7OafDVjAQCoElutyacta24C3ylAIY4X5phPHFX8OEBD4AQfSvfSEUBuy7O/qJyfpj37o3REOwmRVM289KbCbkJp5KpizYOH5esJlVLbQkyrozvOenHHSuy2hwf7ZWkCrLo7I9U8684WfvwdqEhFmkBCA+ft3IE7JJ53RCc/pVLmiV4DSIdskyYgOuztTervfJG/BwFKk0qICMBf3vsgqruOfZ8C0Ru/rrkMBQpwBY+VZFsUAREDDxaaH3Do+yaAXcaOtcHWtV3ZFyDxjnBSdiVgvmjGJ2ad/FjkXeeTFpmdwLMIBzNRet7/lWTAefUDHM83hVbZYdbJXCOAIkv3TVHqVLthhPs+3z9pzM2D+O61DHxYA4e9ed08dyXAlS92D5NsSC9fGiFZcPj3JIBd4KsIlw9IiNcvJ4gen+F+wWHnWQNQYqHUXj2QOLRryPaDAyRJqerCSLJJEYAvToJGvlgIyOTaiY9nkZgNxbO9GGCz3382/wOmLf6PEFyLcAAAAABJRU5ErkJggg==");
    }

    &[data-type="minecraft"] {
        background-image: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAZJJREFUWAntl7FKA0EQhk8RRCwCgo1iYxXQ1s4yhckz+CI2tj6GlYUvoI0WQsgTKFxvEgKJdkFEJDIb/mFvWcaZ3RWbpMjs7c78/8dk7pJU1eqV0YHz6/aie7W9yJCo1lKKyZjqdo8+XHn9OHPx/mJu1jMVhMbO1XtLAVEB/GbsMbilBUQEsBqngEQBco0tIA2A0sYaEAYgc0x1WFj6mmYEd8y6Lz592fIv/2SNAYX4BhaIgCjdjdAYftyBw9s59lwkEMA0DowXZByaf920WKXRgV5/0x3cnX5yAiCsHQlNSfDkacfpDqpv1m8AYDcHRDKGvh+jAEiwgFiN4SECIEkCeZu8Io0jWs0bwkIFgPoYCM4oWoxRZwJAUQiSYgytJAAUE0g9Wk429qyRnwPWwlL5K4B/7wB/HdNnenl24H5stvf1g1WP3tXjMBguH8EPz2P25YWvYgHRAMSM4RcFwKEGRAKQjOEhAiBJAokBaIyhrQJAcgzEB7AYQ9MEgCIfhABSjKGVFQmkc7yX9d8wC6BE8Q/RxsNKuOYk3AAAAABJRU5ErkJggg==");
    }

    &[data-type="hypixel_minecraft"] {
        background-image: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAA6FJREFUWAnFlk1IVFEUx8+Y2cyoTc1AMTrZ10RNo5AMbfoCaxEVYdHGRbjONkbYok3QTshdodTWlhqJWBvLKAsizJIxiYTU/CDD0cEc01J759w5b97HffPGmaAL792vc8/vf8+7H88BWaRw5aE12bDB/g8OWbtV25qS8mWdRoDWMfa9ur8X3ME6GpoYboGZvj9w4cGozJVlG8IdStIJYPDLe3sgNrACQ6+/wc3BVdWJFo7g6zVvoaggT/S7vKqdXYHhaKcKQOcILgxdpfGFIYCnnde0JtSOs3UHQQfvUeDaKJGhxUsLRxMSgPC+Z000ZGGo2WKoaMao+CItqk3A7waYU6tpC0Y4GuczXAbe5S0AmEp9Aq13DD3CW+ecGc1eBicB+IqcaoCqxRj8XF6Flt7zkPj6nVgl5T6AwR9U5lfo6A4uZpxbwdFBHn47fPA77vRugrpjneDevZ2cezxKeHNM6eDoOrmElYkqItpWilURdlwM/8HS9ALt4DoBMmA8njA15xeJswbh6T5HJnB0rm5DE8mmoeJ0GXj2b5BaZQrHwSYBruKNALElcjwZnTEBEOoKbFPap0192LAeONqrawAr2oQLcSS2TE24VfHhfofzEhd1+XrhONgUAfy2i/O/YXF8Go6c8MMZZSf4Iimz+OcVOgl1ZKWSDVwq4NNEAiIHPOQ/XBukXDtjV6Cd2rSvbOHoIzW1pEda3RW+5HcWjWu/zNCkOUTf9wPealxfb24SEFLgnG6c7KLjlvc7Rmd8KgFNb8Qa2Hq4LSc4ckwCGI55fX2luOfn+D5wQhUkACNiB7/cuo8W7exwnFx23Z6WRimtABbDVy3vBKe/2XLmDC4NC15peAu7IUFGIRkJYA+Y3x0alcKNYO0YLJdXy4VYCsBtGKguAdD8alktOITzjI1gY10rBKNBAji0bDwyMQ8+2MxVynPZajpHhooagZ4mP3zsmAWEG5MdvPwRQBTEQWkXiWiH+H1aahdoeuMiq2oA+tvtezhBfDzvxzsmM97nZ1+Iq/mJskswGYUw+PiAOOS6YYHs1Ahg7VbtO7jz/Bz0NvZTZ/DKY+mCo06Ll1EIb0MGG4epAmirKRcOHj4oIt1WMzqR1VnIWEzWm2rT3YYoAn/NcoWn3NuXdALQ3Gqr2bvKzkJ3PNqt9nSIxovi6C3z6v8Tx2L637ruL2LxdUcnia0KyAWuFWYUwgKMYB5DAv4VnJ1izkKswKotwtXKfyj8BXmsex3oy3yEAAAAAElFTkSuQmCC");
    }
}
</style>
