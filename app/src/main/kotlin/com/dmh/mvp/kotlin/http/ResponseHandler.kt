package com.dmh.mvp.kotlin.http

import android.widget.Toast
import com.dmh.mvp.kotlin.R
import com.dmh.mvp.kotlin.base.App
import com.google.gson.JsonObject
import java.net.HttpURLConnection
import java.util.*

/**
 * Created by QiuGang on 2017/9/24 20:20
 * Email : 1607868475@qq.com
 */
abstract class ResponseHandler<T> constructor(val dataTypeClass: Class<T>? = JsonObject::class.java as? Class<T>) {
    var ok = false
    private val tagMap: HashMap<String, Any>

    init {
        if (dataTypeClass == null) {
            throw NullPointerException()
        }
        tagMap = HashMap<String, Any>()
    }

    protected fun setTag(key: String, tag: Any) {
        tagMap.put(key, tag)
    }

    protected fun getTag(key: String): Any? {
        if (tagMap.containsKey(key)) {
            return tagMap[key]
        }
        return null
    }

    abstract fun onStart()

    abstract fun onSuccess(responseCode: Int, apiResponse: ApiResponse, data: T?)

    open fun onFailed(reqUrl: String, errCode: Int, errMsg: String) {
        when (errCode) {
            -1 -> showMsg(errMsg)
            -2 -> showMsg(R.string.toast_error_network_connection_not_available)
            HttpURLConnection.HTTP_CLIENT_TIMEOUT -> showMsg(R.string.toast_error_request_time_out)
            HttpURLConnection.HTTP_INTERNAL_ERROR, HttpURLConnection.HTTP_FORBIDDEN -> showMsg(R.string.toast_error_internal_error)
            else -> showMsg(R.string.toast_error_request_error)
        }
    }

    abstract fun onComplete()

    private fun showMsg(msg: String) {
        Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showMsg(msg: Int) {
        Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT).show()
    }
}
