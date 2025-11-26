plugins {
    `kotlin-conventions`
    id("com.vanniktech.maven.publish") version "0.35.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("io.ktor:ktor-client-core:3.3.3")
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("io.github.blackbaroness", "vk-sdk-kotlin", rootProject.version.toString())

    pom {
        name.set("VK SDK Kotlin")
        description.set("Modern VK SDK, built with Ktor. Non-blocking and fast.")
        inceptionYear.set("2025")
        url.set("https://github.com/BlackBaroness/vk-sdk-kotlin")
        licenses {
            license {
                name.set("The MIT License")
                url.set("https://opensource.org/license/mit")
                distribution.set("https://opensource.org/license/mit")
            }
        }
        developers {
            developer {
                id.set("blackbaroness")
                name.set("BlackBaroness")
                url.set("https://github.com/BlackBaroness")
            }
        }
        scm {
            url.set("https://github.com/BlackBaroness/vk-sdk-kotlin")
            connection.set("scm:git:git://github.com/BlackBaroness/vk-sdk-kotlin.git")
            developerConnection.set("scm:git:ssh://git@github.com/BlackBaroness/vk-sdk-kotlin.git")
        }
    }
}
