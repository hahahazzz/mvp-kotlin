package com.dmh.mvp.kotlin.http

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import com.dmh.mvp.kotlin.BuildConfig
import com.dmh.mvp.kotlin.base.App
import com.dmh.mvp.kotlin.utils.LogUtils
import com.dmh.mvp.kotlin.utils.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonNull
import okhttp3.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.Executor
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLException
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * @Author : QiuGang
 * *
 * @Email : 1607868475@qq.com
 * *
 * @Date : 2017/7/6 14:57
 */
class Api private constructor() {
    private val keyToken = "token"

    private val timeOut = if (DEBUG_MODE) 15L else 60L

    private val requestClient: OkHttpClient
    private val resultCallbackHandler: Handler
    private val executor: Executor
    private val jsonParse: Gson

    init {
        val builder = OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
        if (DEBUG_MODE) {
            trustAllHttps(builder)
            builder.addInterceptor(LogInterceptor.getInstance())
        }
        requestClient = builder.build()

        resultCallbackHandler = Handler(Looper.getMainLooper())
        // 来自OkHttp#ConnectionPool
        executor = ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, SynchronousQueue<Runnable>())

        jsonParse = GsonBuilder().serializeNulls().create()
    }

    fun <T> requestPost(url: String, tag: Any?, handler: ResponseHandler<T>) {
        requestPost(url, tag, mapOf(), handler)
    }

    fun <T> requestPost(url: String, tag: Any?, params: Map<String, String>, handler: ResponseHandler<T>) {
        var tempUrl = url
        val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        if (tag != null) {
            if (tag is Map<*, *>) {
                throw RuntimeException()
            }
        }
        addTokenToParams(params)
        if (params.isNotEmpty()) {
            for ((key, value) in params) {
                if (key.isNullOrEmpty() || value.isNullOrEmpty()) {
                    LogUtils.w("An unexpected Null parameter appears in the request[POST $tempUrl] parameter:key=$key and value=$value")
                    continue
                }
                multipartBodyBuilder.addFormDataPart(key, value)
            }
        } else {
            // 解决java.lang.IllegalStateException: Multipart body must have at least one part.问题
            multipartBodyBuilder.addFormDataPart("", "")
        }
        tempUrl = checkUrlPrefix(tempUrl)
        val reqBuilder = Request.Builder().url(tempUrl).post(multipartBodyBuilder.build())
        reqBuilder.addHeader(keyToken, "")
        executeRequest(reqBuilder.build(), handler)
    }

    fun <T> requestGet(url: String, tag: Any, handler: ResponseHandler<T>) {
        requestGet(url, tag, mapOf(), handler)
    }

    fun <T> requestGet(url: String, tag: Any?, params: Map<String, String>, handler: ResponseHandler<T>) {
        var tempUrl = url
        tempUrl = checkUrlPrefix(tempUrl)
        addTokenToParams(params)
        tempUrl = addParamsToUrl(tempUrl, params)
        val requestBuilder = Request.Builder().url(tempUrl).get()
        requestBuilder.addHeader(keyToken, "")
        tag?.let {
            if (tag is Map<*, *>) {
                throw RuntimeException()
            }
            requestBuilder.tag(it.javaClass.name)
        }
        executeRequest(requestBuilder.build(), handler)
    }

    private fun addParamsToUrl(url: String, params: Map<String, String>): String {
        if (params.isEmpty()) {
            return url
        }
        val paramsBuilder = StringBuilder(url)
        paramsBuilder.append("?")
        for ((key, value) in params) {
            if (key.isNullOrEmpty() || value.isNullOrEmpty()) {
                LogUtils.w("An unexpected Null parameter appears in the request[GET $url] parameter:key=$key and value=$value")
                continue
            }
            paramsBuilder.append(key).append("=").append(value).append("&")
        }
        return paramsBuilder.substring(0, paramsBuilder.length - 1)
    }

    private fun addTokenToParams(params: Map<String, String>) {
    }


    private fun checkUrlPrefix(url: String?): String {
        url?.let { }
        if (url == null || url.startsWith("http")) {
            return API_HOST
        }
        var tempUrl = url
        if (url.startsWith("/")) {
            tempUrl = url.substring(1)
        }
        tempUrl = API_HOST + tempUrl
        return tempUrl
    }

    fun cancelRequest(tag: Any) {
        val dispatcher = requestClient.dispatcher()
        if (TAG_CANCEL_ALL_REQUEST == tag.toString()) {
            dispatcher.cancelAll()
            return
        }
        val tagName = tag.javaClass.name
        dispatcher.runningCalls()
                .filter { tagName == it.request().tag() }
                .forEach { it.cancel() }

        dispatcher.queuedCalls()
                .filter { tagName == it.request().tag() }
                .forEach { it.cancel() }
    }

    private fun <T> executeRequest(request: Request, responseHandler: ResponseHandler<T>) {
        if (!networkAvailable()) {
            responseHandler.onFailed(ErrorInfo(request.url().toString(), -2, "The network connection is not available"))
            return
        }
        val call = requestClient.newCall(request)
        executeCall(call, responseHandler)
    }

    private fun <T> executeCall(call: Call, responseHandler: ResponseHandler<T>) {
        val request = call.request()
        val reqUrl = request.url().toString()
        responseHandler.start()
        executor.execute {
            var response: Response? = null
            try {
                response = call.execute()
                if (response == null) {
                    resultCallbackHandler.post {
                        responseHandler.onFailed(ErrorInfo(reqUrl, -1, "Response Null"))
                        responseHandler.complete()
                    }
                } else {
                    if (response.isSuccessful) {
                        val responseCode = response.code()
                        val bodyContent = response.body()!!.string()
                        val responseContent: ResponseContent = jsonParse.fromJson(bodyContent)
                        val data: T? = if (responseContent.result == null || responseContent.result is JsonNull) {
                            null
                        } else {
                            jsonParse.fromJson(responseContent.result, responseHandler.typeClass)
                        }
                        resultCallbackHandler.post {
                            responseHandler.success(SuccessInfo(responseCode, responseContent, data))
                            responseHandler.complete()
                        }
                    } else {
                        val errCode = response.code()
                        val errMsg = response.message()
                        resultCallbackHandler.post {
                            responseHandler.onFailed(ErrorInfo(reqUrl, errCode, errMsg))
                            responseHandler.complete()
                        }
                    }
                }
            } catch (e: IOException) {
                if (DEBUG_MODE) {
                    e.printStackTrace()
                }
                val errorCode = if (e is SocketTimeoutException || e is SSLException)
                    HttpURLConnection.HTTP_CLIENT_TIMEOUT
                else
                    -1
                val errMsg = e.message ?: ""
                resultCallbackHandler.post {
                    responseHandler.onFailed(ErrorInfo(reqUrl, errorCode, errMsg))
                    responseHandler.complete()
                }
            } finally {
                try {
                    response?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun trustAllHttps(builder: OkHttpClient.Builder) {
        val trustManager = object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf<X509Certificate>()
            }
        }
        try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            builder.sslSocketFactory(sslSocketFactory, trustManager)
                    .hostnameVerifier { _, _ -> true }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }

    }

    internal class LogInterceptor private constructor() : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)

            val requestUrl = request.url().toString()
            val requestMethod = request.method()

            val responseCode = response.code()
            val responseMessage = response.message()
            val responseBody = response.body()!!.string()

            val logBuilder = StringBuilder()
            logBuilder.append(requestName)
            logBuilder.append(LINE_SEPARATOR)
            logBuilder.append(TAB)
            logBuilder.append(requestMethod)
            logBuilder.append(TAB)
            logBuilder.append(requestUrl)
            logBuilder.append(LINE_SEPARATOR)

            logBuilder.append(responseName)
            logBuilder.append(LINE_SEPARATOR)
            logBuilder.append(TAB)
            logBuilder.append("statusCode=").append(responseCode)
            logBuilder.append(TAB)
            logBuilder.append("message=").append(responseMessage)
            logBuilder.append(TAB)
            logBuilder.append("body=")
            logBuilder.append(LINE_SEPARATOR).append(responseBody)
            LogUtils.d(logBuilder.toString())
            return response.newBuilder().body(ResponseBody.create(response.body()!!.contentType(), responseBody))
                    .build()
        }

        companion object {
            private val LINE_SEPARATOR = "\n"
            private val TAB = "\t"

            private val requestName = Request::class.java.simpleName
            private val responseName = Response::class.java.simpleName

            private val interceptor: LogInterceptor by lazy { LogInterceptor() }
            fun getInstance(): LogInterceptor = interceptor
        }
    }

    companion object {
        val API_HOST = String.format("https://192.168.1.182:8443/%s/", "v1.0")
        val TAG_CANCEL_ALL_REQUEST = "cancelAllRequest"

        private val instance: Api by lazy { Api() }

        private val DEBUG_MODE = BuildConfig.DEBUG

        fun getApi(): Api = instance

        private val cm: ConnectivityManager by lazy { App.get().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

        private fun networkAvailable(): Boolean {
            return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isAvailable
        }
    }
}
