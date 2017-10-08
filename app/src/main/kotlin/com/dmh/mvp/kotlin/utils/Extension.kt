package com.dmh.mvp.kotlin.utils

import com.google.gson.Gson

/**
 * Created by QiuGang on 2017/10/7 22:29
 * Email : 1607868475@qq.com
 */
inline fun <reified T> Gson.fromJson(json: String): T {
    return this.fromJson(json, T::class.java)
}