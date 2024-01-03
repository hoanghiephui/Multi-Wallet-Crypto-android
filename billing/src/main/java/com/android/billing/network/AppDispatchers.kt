package com.android.billing.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val appDispatcher: AppDispatcher)

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainImmediateDispatcher

enum class AppDispatcher {
    Default,
    IO,
    Main,
}
