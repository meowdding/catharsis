plugins {
    id("me.owdding.resources")
    id("repo-shas")
}

project.layout.buildDirectory.set(rootProject.layout.buildDirectory)

compactingResources {
    basePath = ".."
}
