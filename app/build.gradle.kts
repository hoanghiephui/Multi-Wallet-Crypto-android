plugins {
    id("compose.android.application")
    id("compose.android.compose")
    id("android.application.flavors")
    id("compose.android.hilt")
    id("android.room")
    id("kotlin-parcelize")
    //id("compose.androidFirebase")
}

android {
    defaultConfig {
        applicationId = "io.horizontalsystems"
        versionCode = 85
        versionName = "0.34.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("room.incremental", true)
            }
        }
        vectorDrawables {
            useSupportLibrary = true
        }

        resValue("string", "companyWebPageLink", "https://horizontalsystems.io")
        resValue("string", "appWebPageLink", "https://unstoppable.money")
        resValue("string", "analyticsLink", "https://unstoppable.money/analytics")
        resValue("string", "appGithubLink", "https://github.com/horizontalsystems/unstoppable-wallet-android")
        resValue("string", "appTwitterLink", "https://twitter.com/UnstoppableByHS")
        resValue("string", "appTelegramLink", "https://t.me/unstoppable_announcements")
        resValue("string", "appRedditLink", "https://www.reddit.com/r/UNSTOPPABLEWallet/")
        resValue("string", "reportEmail", "support.unstoppable@protonmail.com")
        resValue("string", "releaseNotesUrl", "https://api.github.com/repos/horizontalsystems/unstoppable-wallet-android/releases/tags/")
        resValue("string", "walletConnectAppMetaDataName", "Unstoppable")
        resValue("string", "walletConnectAppMetaDataUrl", "unstoppable.money")
        resValue("string", "walletConnectAppMetaDataIcon", "https://raw.githubusercontent.com/horizontalsystems/HS-Design/master/PressKit/UW-AppIcon-on-light.png")
        resValue("string", "accountsBackupFileSalt", "unstoppable")
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            resValue("string", "twitterBearerToken", "AAAAAAAAAAAAAAAAAAAAAJgeNwEAAAAA6xVpR6xLKTrxIA3kkSyRA92LDpA%3Da6auybDwcymUyh2BcS6zZwicUdxGtrzJC0qvOSdRwKLeqBGhwB")
            resValue("string", "cryptoCompareApiKey", "2b08fe1dba559ca6acf5e1897b6de8749cee0ace6052d7aa7fccf6aa9f1b3255")
            resValue("string", "uniswapGraphUrl", "https://api.thegraph.com/subgraphs/name/uniswap/uniswap-v2")
            resValue("string", "infuraProjectId", "2a1306f1d12f4c109a4d4fb9be46b02e")
            resValue("string", "infuraSecretKey", "fc479a9290b64a84a15fa6544a130218")
            resValue("string", "etherscanKey", "GKNHXT22ED7PRVCKZATFZQD1YI7FK9AAYE")
            resValue("string", "bscscanKey", "5ZGSHWYHZVA8XZHB8PF6UUTRNNB4KT43ZZ")
            resValue("string", "polygonscanKey", "TNQ44BCF1MS3S75Y6A2B6SN88I8UYFJFRM")
            resValue("string", "snowtraceApiKey", "DD8VX77TQ73KSNDFGBQQ31J7K5B51CXXPH")
            resValue("string", "optimisticEtherscanApiKey", "A4E6DAX46FFFW4CGZP6IMZ64ADI3TIRBTS")
            resValue("string", "arbiscanApiKey", "Z43JN5434XVNA5D73UGPWKF26G5D9MGDPZ")
            resValue("string", "gnosisscanApiKey", "V2J8YU15ZX9S1W3GTUV2HXM11TP2TUBRW4")
            resValue("string", "ftmscanApiKey", "57YQ2GIRAZNV6M5HIJYYG3XQGGNIPVV8MF")
            resValue("string", "defiyieldProviderApiKey", "bc3a9319-b115-4673-ace9-03228c11d026")
            resValue("string", "is_release", "false")
            resValue("string", "guidesUrl", "https://raw.githubusercontent.com/horizontalsystems/blockchain-crypto-guides/develop/index.json")
            resValue("string", "faqUrl", "https://raw.githubusercontent.com/horizontalsystems/Unstoppable-Wallet-Website/master/src/faq.json")
            resValue("string", "coinsJsonUrl", "https://raw.githubusercontent.com/horizontalsystems/cryptocurrencies/master/coins.json")
            resValue("string", "providerCoinsJsonUrl", "https://raw.githubusercontent.com/horizontalsystems/cryptocurrencies/master/provider.coins.json")
            resValue("string", "marketApiBaseUrl", "https://api-dev.blocksdecoded.com")
            resValue("string", "marketApiKey", "IQf1uAjkthZp1i2pYzkXFDom")
            resValue("string", "openSeaApiKey", "cc98f68d836b4c8c8ab8f894b6e2aae8")
            resValue("string", "walletConnectV2Key", "8b4f41c60880a3e3ad57d82fddb30568")
            resValue("string", "solscanApiKey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVkQXQiOjE2Nzk0NjQyMTQ4NDAsImVtYWlsIjoiaHJ6c3lzdGVtczEwMUBnbWFpbC5jb20iLCJhY3Rpb24iOiJ0b2tlbi1hcGkiLCJpYXQiOjE2Nzk0NjQyMTR9.BRM7J9RbDpHgd2oMAus00XfWOxTJgV2Tn2_chXZOdtk")
            resValue("string", "trongridApiKey", "33374494-8060-447e-8367-90c5efd4ed95")
        }

        release {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "twitterBearerToken", "AAAAAAAAAAAAAAAAAAAAAJgeNwEAAAAA6xVpR6xLKTrxIA3kkSyRA92LDpA%3Da6auybDwcymUyh2BcS6zZwicUdxGtrzJC0qvOSdRwKLeqBGhwB")
            resValue("string", "cryptoCompareApiKey", "")
            resValue("string", "uniswapGraphUrl", "https://api.thegraph.com/subgraphs/name/uniswap/uniswap-v2")
            resValue("string", "infuraProjectId", "5bf760228fcd47bb8d277dba49b7b369")
            resValue("string", "infuraSecretKey", "7b81c992c98d4e60aaa8a0ef9acff2be")
            resValue("string", "etherscanKey", "TTH1114D5VD5ZMCJZ4B74SGIDRCGSKWGX9")
            resValue("string", "bscscanKey", "HBQQN4GTKCHYSRZCKFVQJ3FWGPY4T8237Y")
            resValue("string", "polygonscanKey", "2JM7USE5YRI59RWFZQI2RECAZSNI5QEQGV")
            resValue("string", "snowtraceApiKey", "47IXTRAAFT1E1J4RNSPZPNB5EWUIQR16FG")
            resValue("string", "optimisticEtherscanApiKey", "745EUI4781T147M9QJRNS5G3Q5NFF2SJXP")
            resValue("string", "arbiscanApiKey", "4QWW522BV13BJCZMXH1JIB2ESJ7MZTSJYI")
            resValue("string", "gnosisscanApiKey", "KEXFAQKDUENZ5U9CW3ZKYJEJ84ZIHH9QTY")
            resValue("string", "ftmscanApiKey", "JAWRPW27KEMVXMJJ9UKY63CVPH3X5V9SMP")
            resValue("string", "defiyieldProviderApiKey", "bc3a9319-b115-4673-ace9-03228c11d026")
            resValue("string", "is_release", "true")
            resValue("string", "guidesUrl", "https://raw.githubusercontent.com/horizontalsystems/blockchain-crypto-guides/v1.2/index.json")
            resValue("string", "faqUrl", "https://raw.githubusercontent.com/horizontalsystems/Unstoppable-Wallet-Website/v1.3/src/faq.json")
            resValue("string", "coinsJsonUrl", "https://raw.githubusercontent.com/horizontalsystems/cryptocurrencies/v0.21/coins.json")
            resValue("string", "providerCoinsJsonUrl", "https://raw.githubusercontent.com/horizontalsystems/cryptocurrencies/v0.21/provider.coins.json")
            resValue("string", "marketApiBaseUrl", "https://api.blocksdecoded.com")
            resValue("string", "marketApiKey", "IQf1uAjkthZp1i2pYzkXFDom")
            resValue("string", "openSeaApiKey", "cc98f68d836b4c8c8ab8f894b6e2aae8")
            resValue("string", "walletConnectV2Key", "0c5ca155c2f165a7d0c88686f2113a72")
            resValue("string", "solscanApiKey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjcmVhdGVkQXQiOjE2Nzk0NjQxMTQ0ODUsImVtYWlsIjoiaHNkYW84ODg4QGdtYWlsLmNvbSIsImFjdGlvbiI6InRva2VuLWFwaSIsImlhdCI6MTY3OTQ2NDExNH0.91DUjjjYu86f1ZMMJ5cyJxIKTTw_srhI-vNgYaTCPUU")
            resValue("string", "trongridApiKey", "8f5ae2c8-8012-42a8-b0ca-ffc2741f6a29")
        }

    }
    packaging {
        resources {
            pickFirsts += setOf("META-INF/atomicfu.kotlin_module")
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE.md",
                "META-INF/NOTICE.md"
            )
        }
    }
    namespace = "io.horizontalsystems.bankwallet"
    lint.disable += "LogNotTimber"

    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.bouncycastle:bcprov-jdk15to18:1.68")).using(module("org.bouncycastle:bcprov-jdk15on:1.65"))
            substitute(module("com.google.protobuf:protobuf-java:3.6.1")).using(module("com.google.protobuf:protobuf-javalite:3.21.1"))
            substitute(module("net.jcip:jcip-annotations:1.0")).using(module("com.github.stephenc.jcip:jcip-annotations:1.0-1"))

            substitute(module("com.tinder.scarlet:scarlet:0.1.12")).using(module("com.github.WalletConnect.Scarlet:scarlet:1.0.0"))
            substitute(module("com.tinder.scarlet:websocket-okhttp:0.1.12")).using(module("com.github.WalletConnect.Scarlet:websocket-okhttp:1.0.0"))
            substitute(module("com.tinder.scarlet:stream-adapter-rxjava2:0.1.12")).using(module("com.github.WalletConnect.Scarlet:stream-adapter-rxjava2:1.0.0"))
            substitute(module("com.tinder.scarlet:message-adapter-gson:0.1.12")).using(module("com.github.WalletConnect.Scarlet:message-adapter-gson:1.0.0"))
            substitute(module("com.tinder.scarlet:lifecycle-android:0.1.12")).using(module("com.github.WalletConnect.Scarlet:lifecycle-android:1.0.0"))
        }
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.preference.ktx)
    //Splash screen
    implementation(libs.androidx.core.splashscreen)

    //AppWidgets
    implementation(libs.androidx.glance.appwidget)
    // optional - ReactiveStreams support for LiveData
    implementation(libs.androidx.lifecycle.reactivestreams.ktx)

    implementation(libs.android.material)

    implementation(libs.rxjava)
    implementation(libs.rxandroid)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.adapter.rxjava2)
    implementation(libs.retrofit.adapter.converter.gson)
    implementation(libs.retrofit.adapter.converter.scalars)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.androidx.biometric)

    implementation(libs.commonmark)
    implementation(libs.core)
    //Custom tabs, chrome
    implementation(libs.androidx.browser)

    // Navigation component
    implementation(libs.androidx.navigation.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    //Compose Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.navigation.animation)

    api(libs.zxing.android.embedded)

    // WorkManager Kotlin
    implementation(libs.androidx.work.ktx)
    implementation(libs.androidx.work.rxjava2)



    // Wallet kits
    implementation(libs.bitcoin.kit.android)
    implementation(libs.ethereum.kit.android)
    implementation(libs.blockchain.fee.rate.kit.android)
    implementation(libs.binance.chain.kit.android)
    implementation(libs.market.kit.android)
    implementation(libs.solana.kit.android)
    implementation(libs.tron.kit.android)
    // Zcash SDK
    implementation(libs.zcash.android.sdk)
    implementation("io.github.binance:binance-connector-java:3.0.0rc2") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
    }

    // WalletConnect
    implementation(libs.okhttp)
    // WalletConnect V2
    implementation(platform(libs.walletconnect.bom))
    implementation(libs.walletconnect.sign)
    implementation(libs.walletconnect.android.core)

    // Unstoppable Domains
    implementation(libs.resolution.java)

    // Ethereum Name Service
    implementation(libs.web3j.core)

    // in case native file tor.so not loading, do full gradle clean and build.
    implementation(libs.tor.kit.android)
    implementation(libs.twitter.text)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.rx2)

    // UI modules

    implementation(project(":core"))
    implementation(project(":components:icons"))
    implementation(project(":components:chartview"))
    implementation(project(":designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:datastore"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))

    // Integration with activities
    implementation(libs.androidx.activity.compose)
    // Compose Material Design
    implementation(libs.androidx.compose.material)
    // Animations
    implementation(libs.androidx.compose.animation)
    // Tooling support (Previews, etc.)
    implementation(libs.androidx.compose.ui.tooling)
    // Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewModelCompose)

    implementation(libs.androidx.compose.runtime.livedata)

    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)
    implementation(libs.coil.kt.gif)
    implementation(libs.coil.kt)
    // When using a AppCompat theme
    implementation(libs.accompanist.appcompat.theme)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.window.manager)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.kotlinx.coroutines.guava)

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0")

    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // optional - Test helpers for LiveData
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.mockito:mockito-core:3.3.3")
    testImplementation("com.nhaarman:mockito-kotlin-kt1.1:1.6.0")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.7")
    testImplementation("org.powermock:powermock-module-junit4:2.0.7")

    // Spek
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.9")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.9")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
}