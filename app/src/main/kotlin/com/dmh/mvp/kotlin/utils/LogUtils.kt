package com.dmh.mvp.kotlin.utils

import com.dmh.mvp.kotlin.BuildConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 * Created by QiuGang on 2017/9/30 12:43
 * Email : 1607868475@qq.com
 */
object LogUtils {
    private val DEBUG = BuildConfig.DEBUG

    init {
        if (DEBUG) {
            Logger.addLogAdapter(AndroidLogAdapter())
        }
    }

    fun d(msg: String, vararg args: Any) {
        if (DEBUG) {
            Logger.d(msg, args)
        }
    }

    fun d(number: Number) {
        if (DEBUG) {
            Logger.d(number.toString())
        }
    }

    fun w(msg: String, vararg args: Any) {
        if (DEBUG) {
            Logger.w(msg, args)
        }
    }

    fun e(msg: String, vararg args: Any) {
        if (DEBUG) {
            Logger.e(msg, args)
        }
    }

    fun out(msg: String, vararg args: Any) {
        if (DEBUG) {
            println(String.format(msg, args))
        }
    }
}