package com.android.billing

import android.util.Log
import timber.log.Timber
import java.util.Locale

class DebugTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Log.println(priority, "AppLog", "$message ${getCallerInfo()}")
    }

    @Suppress("ThrowingExceptionsWithoutMessageOrCause")
    private fun getCallerInfo(): String {
        val stackTrace = Throwable().stackTrace

        if (stackTrace.size < 6) {
            return ""
        }

        val element = stackTrace[5]

        return String.format(Locale.getDefault(), "%s(%s:%s)", element.methodName, element.fileName, element.lineNumber)
    }
}
