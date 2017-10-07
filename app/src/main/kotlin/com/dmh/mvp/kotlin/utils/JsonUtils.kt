package com.dmh.mvp.kotlin.utils

import com.google.gson.Gson
import com.google.gson.JsonElement

/**
 * Created by QiuGang on 2017/9/30 12:34
 * Email : 1607868475@qq.com
 */
class JsonUtils private constructor() {
    private val gson: Gson by lazy { Gson() }

    companion object {
        private val instance: JsonUtils by lazy { JsonUtils() }
        fun get(): JsonUtils = instance
    }

    fun toJson(params: Any) = gson.toJson(params)

    fun <T> fromJson(json: String, cls: Class<T>): T = gson.fromJson(json, cls)

    fun <T> fromJson(json: JsonElement, cls: Class<T>): T = gson.fromJson(json, cls)
}