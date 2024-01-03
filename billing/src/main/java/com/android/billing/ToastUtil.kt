package com.android.billing

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtil {

    private var currentToast: Toast? = null

    fun show(
        context: Context,
        text: String,
        duration: Int = Toast.LENGTH_SHORT,
        doCancel: Boolean = true,
    ) {
        if (doCancel) cancel()
        currentToast = Toast.makeText(context, text, duration)
        currentToast?.show()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            currentToast?.addCallback(
                object : Toast.Callback() {
                    override fun onToastHidden() {
                        currentToast = null
                    }
                },
            )
        }
    }

    fun show(
        context: Context,
        @StringRes textId: Int,
        vararg formatArgs: Any,
        duration: Int = Toast.LENGTH_SHORT,
        doCancel: Boolean = true,
    ) {
        show(context, context.getString(textId, *formatArgs), duration, doCancel)
    }

    fun cancel() {
        currentToast?.cancel()
    }
}
