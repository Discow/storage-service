import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    download.set(false)
}

tasks.register<NpmTask>("build") {
    group = "build"
    args.set(listOf("run", "build"))
}
