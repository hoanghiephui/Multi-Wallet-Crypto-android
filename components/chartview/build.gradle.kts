plugins {
    id("compose.android.library")
    id("compose.library.compose")
}

android {
    defaultConfig {
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        viewBinding =  true
    }
    namespace = "io.horizontalsystems.chartview"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.10.1")
}
