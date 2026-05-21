plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                // api() instead of implementation() so Ktor types are visible
                // to KSP in consumer modules (mobile-takmir, mobile-jemaah)
                api(libs.ktor.client.core)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.ktor.client.logging)
                api(libs.ktor.client.auth)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        val androidMain by getting {
            dependencies {
                // api() so Android engine type is visible to KSP in app modules
                api(libs.ktor.client.android)
            }
        }
    }
}

android {
    namespace = "com.masjid.core"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
