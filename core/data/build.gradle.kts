plugins {
    id("compose.android.library")
    id("compose.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "coin.chain.crypto.core.data"
}

dependencies {
    //implementation(project(":core:analytics"))
    implementation(project(":core:common"))
    //implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:model"))
    //implementation(project(":core:network"))
    //implementation(project(":core:notifications"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}