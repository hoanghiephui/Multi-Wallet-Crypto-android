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
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
}
