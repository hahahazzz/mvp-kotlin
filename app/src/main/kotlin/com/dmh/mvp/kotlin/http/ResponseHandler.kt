package com.dmh.mvp.kotlin.http

import android.widget.Toast
import com.dmh.mvp.kotlin.base.App
import com.dmh.mvp.kotlin.R
import com.google.gson.JsonObject
import java.net.HttpURLConnection

/**
 * Created by QiuGang on 2017/9/24 20:20
 * Email : 1607868475@qq.com
 */
class ResponseHandler<T>
constructor(val typeClass: Class<T>? = JsonObject::class.java as Class<T>,
            val start: () -> Unit = {},
            val success: (result: SuccessInfo<T>) -> Unit,
            val complete: () -> Unit = {},
            private val fail: (errorInfo: ErrorInfo) -> Boolean = { false }) {

    fun onFailed(errorInfo: ErrorInfo) {
        if (fail(errorInfo)) {
            return
        }
        when (errorInfo.errorCode) {
            -1 -> showMsg(errorInfo.errorMessage)
            -2 -> showMsg(R.string.toast_error_network_connection_not_available)
            HttpURLConnection.HTTP_CLIENT_TIMEOUT -> showMsg(R.string.toast_error_request_time_out)
            HttpURLConnection.HTTP_INTERNAL_ERROR, HttpURLConnection.HTTP_FORBIDDEN -> showMsg(R.string.toast_error_internal_error)
            else -> showMsg(R.string.toast_error_request_error)
        }
    }

    private fun showMsg(msg: String) {
        Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showMsg(msg: Int) {
        Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show()
    }
}
