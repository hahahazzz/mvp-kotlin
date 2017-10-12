package com.dmh.mvp.kotlin.http

import com.google.gson.JsonElement

/**
 * Created by QiuGang on 2017/9/24 22:23
 * Email : 1607868475@qq.com
 */
/**
 * 服务器返回内容的body内容
 */
data class ResponseContent(var statusCode: Int = 0/*返回信息体自定的状态码*/,
                           var result: JsonElement? = null,
                           var msg: String = "")

data class SuccessInfo<T>(var serverResponseCode: Int = 0,/*服务器响应状态码*/
                          var responseContent: ResponseContent,
                          var data: T?)

data class ErrorInfo(val requestUrl: String = "",
                     val errorCode: Int = 0,
                     val errorMessage: String = "")