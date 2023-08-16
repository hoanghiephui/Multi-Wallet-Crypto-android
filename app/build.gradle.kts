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
        applicationId = "io.horizontalsystems.bankwallet"
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
        resValue("string", "walletConnectV1PeerMetaName", "Unstoppable Wallet")
        resValue("string", "walletConnectV1PeerMetaUrl", "https://unstoppable.money")
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
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    // use -ktx for Kotlin
    // alternatively - just LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    // alternatively - Lifecycles only (no ViewModel or LiveData).
    //     Support library depends on this lightweight import
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.fragment:fragment-ktx:1.6.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //Splash screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    //AppWidgets
    implementation("androidx.glance:glance-appwidget:1.0.0-beta01")


    // alternately - if using Java8, use the following instead of compiler
    implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")
    // optional - ReactiveStreams support for LiveData
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:2.5.1")

    implementation("androidx.recyclerview:recyclerview:1.3.0")

    implementation("com.google.android.material:material:1.9.0")

    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    // Because RxAndroid releases are few and far between, it is recommended you also
    // explicitly depend on RxJava's latest version for bug fixes and new features.
    // (see https://github.com/ReactiveX/RxJava/releases for latest 2.x.x version)
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("androidx.biometric:biometric:1.1.0")

    implementation("com.atlassian.commonmark:commonmark:0.17.0")
    implementation("io.noties.markwon:core:4.6.2")

    // ViewPager circle indicator
    implementation("me.relex:circleindicator:2.1.6")

    //Custom tabs, chrome
    implementation("androidx.browser:browser:1.5.0")

    // Navigation component
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    //Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.31.6-rc")

    api("com.journeyapps:zxing-android-embedded:4.3.0")

    // WorkManager Kotlin
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    // WorkManager RxJava2 support
    implementation("androidx.work:work-rxjava2:2.8.1")



    // Wallet kits
    implementation("com.github.horizontalsystems:bitcoin-kit-android:f7a7142")
    implementation("com.github.horizontalsystems:ethereum-kit-android:e714fea")
    implementation("com.github.horizontalsystems:blockchain-fee-rate-kit-android:1d3bd49")
    implementation("com.github.horizontalsystems:binance-chain-kit-android:7e4d7c0")
    implementation("com.github.horizontalsystems:market-kit-android:d345edd")
    implementation("com.github.horizontalsystems:solana-kit-android:f9d9f4a")
    implementation("com.github.horizontalsystems:tron-kit-android:1cc0ebe")
    // Zcash SDK
    implementation("cash.z.ecc.android:zcash-android-sdk:1.14.0-beta01")
    implementation("io.github.binance:binance-connector-java:3.0.0rc2") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
    }

    // WalletConnect
    implementation("com.github.horizontalsystems:wallet-connect-kotlin:b9a50b8")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    // WalletConnect V2
    implementation(platform("com.walletconnect:android-bom:1.13.0"))
    implementation("com.walletconnect:sign")
    implementation("com.walletconnect:android-core")

    // Unstoppable Domains
    implementation("com.github.unstoppabledomains:resolution-java:v6.2.2")

    // Ethereum Name Service
    implementation("org.web3j:core:4.9.0")

    // in case native file tor.so not loading, do full gradle clean and build.
    implementation("com.github.horizontalsystems:tor-kit-android:13750c4")
    implementation("com.twitter.twittertext:twitter-text:3.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.6.4")

    // UI modules

    implementation(project(":core"))
    implementation(project(":components:icons"))
    implementation(project(":components:chartview"))

    // Integration with activities
    implementation("androidx.activity:activity-compose:1.7.2")
    // Compose Material Design
    implementation("androidx.compose.material:material:1.4.3")
    // Animations
    implementation("androidx.compose.animation:animation:1.4.3")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.4.3")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    implementation("androidx.compose.runtime:runtime-livedata:1.4.3")

    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("io.coil-kt:coil-svg:2.3.0")
    implementation("io.coil-kt:coil-gif:2.3.0")

    // When using a AppCompat theme
    implementation("com.google.accompanist:accompanist-appcompat-theme:0.31.6-rc")
    implementation("com.google.accompanist:accompanist-flowlayout:0.31.6-rc")
    implementation("com.google.accompanist:accompanist-permissions:0.31.6-rc")
    implementation("com.google.accompanist:accompanist-pager:0.31.6-rc")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.3")

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