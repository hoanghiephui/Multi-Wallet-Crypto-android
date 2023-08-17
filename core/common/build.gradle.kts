plugins {
    id("compose.android.library")
    id("compose.android.hilt")
}

android {
    namespace = "coin.chain.crypto.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}