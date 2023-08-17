plugins {
    id("compose.android.library")
}

android {
    defaultConfig {
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    namespace = "io.horizontalsystems.icons"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
}
