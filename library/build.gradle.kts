plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 22
        targetSdk = 33

        consumerProguardFile("proguard-markdownview.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "se.ingenuity.android"
            artifactId = "MarkdownView"
            version = "1.1.0-alpha"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    implementation("androidx.annotation:annotation:1.5.0")
    implementation("io.noties.markwon:core:4.6.2")

    compileOnly("com.google.android.material:material:1.7.0")
    compileOnly("androidx.appcompat:appcompat:1.5.1")
}
