plugins {
    id("compose.android.library")
    id("kotlin-parcelize")
}

android {
    namespace = "io.horizontalsystems.core"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.fragment.ktx)
    // Navigation component
    api(libs.androidx.navigation.ktx)
    api(libs.androidx.navigation.ui.ktx)

    implementation(libs.rxjava)
    implementation(libs.androidx.biometric)
    implementation(libs.android.material)

    testImplementation(libs.junit4)
}
