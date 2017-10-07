package com.dmh.mvp.kotlin.utils

import android.os.Debug
import com.dmh.mvp.kotlin.BuildConfig

/**
 * @Author : QiuGang
 * @Email : 1607868475@qq.com
 * @Date : 2017/7/6 13:49
 */
object DebugUtils {
    fun debugConnection() = if (debugMode()) false else Debug.isDebuggerConnected()

    fun debugMode() = BuildConfig.DEBUG

    fun releaseMode() = !debugMode()
}
