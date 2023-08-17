plugins {
    id("compose.android.library")
    id("kotlin-parcelize")
}

android {
    namespace = "io.horizontalsystems.core"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.10.1")

    implementation("androidx.fragment:fragment-ktx:1.6.0")
    // Navigation component
    api("androidx.navigation:navigation-fragment-ktx:2.6.0")
    api("androidx.navigation:navigation-ui-ktx:2.6.0")

    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("com.google.android.material:material:1.9.0")

    testImplementation("junit:junit:4.13.2")
}
