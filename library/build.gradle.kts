plugins {
    id("com.android.library")
    `maven-publish`
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 22
        targetSdk = 32
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            consumerProguardFile("proguard-markdownview.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "se.ingenuity.android"
            artifactId = "MarkdownView"
            version = "1.0.4"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    implementation("androidx.annotation:annotation:1.4.0")
    implementation("io.noties.markwon:core:4.6.2")

    compileOnly("com.google.android.material:material:1.6.1")
    compileOnly("androidx.appcompat:appcompat:1.4.2")
}
