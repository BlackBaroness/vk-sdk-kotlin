plugins {
    `kotlin-conventions`
    `publish-conventions`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("io.ktor:ktor-client-core:3.2.3")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

// local artifact is "io.github.blackbaroness:vk-sdk-kotlin:SNAPSHOT"
group = "io.github.blackbaroness"
version = "SNAPSHOT"
