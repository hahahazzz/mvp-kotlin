package com.dmh.mvp.kotlin.utils

import android.content.Context
import android.net.ConnectivityManager
import com.dmh.mvp.kotlin.base.App


/**
 * Created by QiuGang on 2017/9/24 22:32
 * Email : 1607868475@qq.com
 */
object NetworkUtils {
    private val cm: ConnectivityManager by lazy {
        App.get().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun networkAvailable(): Boolean {
        return cm.activeNetworkInfo.isAvailable
    }
}
