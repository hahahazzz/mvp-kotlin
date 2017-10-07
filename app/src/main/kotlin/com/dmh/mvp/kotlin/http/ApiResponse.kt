package com.dmh.mvp.kotlin.http

import com.google.gson.JsonElement

/**
 * Created by QiuGang on 2017/9/24 22:23
 * Email : 1607868475@qq.com
 */
data class ApiResponse(var code: Int = 0, var result: JsonElement? = null, var msg: String? = null)
