plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "vk-sdk-kotlin"

include(
    //"vk-sdk-kotlin-generator",
    "vk-sdk-kotlin-library"
)
