package com.dmh.mvp.kotlin.http

import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.support.v4.util.ArrayMap
import android.text.TextUtils
import com.dmh.mvp.kotlin.BuildConfig
import com.dmh.mvp.kotlin.base.App
import com.dmh.mvp.kotlin.utils.LogUtils
import com.google.gson.Gson
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
 * @Email : 1607868475@qq.com
 * @Date : 2017/7/6 14:57
 */
class Api private constructor() {
    val KEY_TOKEN = "token"

    private val TIME_OUT = if (DEBUG_MODE) 15L else 60L

    private val requestClient: OkHttpClient
    private val resultCallbackHandler: Handler
    private val executor: Executor
    private val jsonParse: Gson

    init {
        val builder = OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
        if (DEBUG_MODE) {
            trustAllHttps(builder)
            builder.addInterceptor(LogInterceptor.getInstance())
        }
        requestClient = builder.build()

        resultCallbackHandler = Handler(Looper.getMainLooper())
        // 来自OkHttp#ConnectionPool
        executor = ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, SynchronousQueue<Runnable>())

        jsonParse = Gson()
    }

    fun <T> post(url: String, tag: Any?, params: ArrayMap<String, String>?, handler: ResponseHandler<T>) {
        var tempUrl = url
        var tempParams = params
        val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        if (tag != null) {
            if (tag is ArrayMap<*, *>) {
                throw RuntimeException()
            }
        }
        if (tempParams == null) {
            tempParams = ArrayMap<String, String>()
        }
        if (tempParams.size > 0) {
            for ((key, value) in tempParams) {
                if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                    LogUtils.w("An unexpected Null parameter appears in the request[POST $tempUrl] parameter:key=$key and value=$value")
                    continue
                }
                multipartBodyBuilder.addFormDataPart(key, value)
            }
        }
        tempUrl = checkUrlPrefix(tempUrl)
        val reqBuilder = Request.Builder().url(tempUrl).post(multipartBodyBuilder.build())
        executeRequest(reqBuilder.build(), handler)

    }

    operator fun <T> get(url: String, tag: Any, handler: ResponseHandler<T>) {
        get(url, tag, null, handler)
    }

    operator fun <T> get(url: String, tag: Any?, params: ArrayMap<String, String>?, handler: ResponseHandler<T>) {
        var tempUrl = url
        var tempParams = params
        tempUrl = checkUrlPrefix(tempUrl)
        if (tempParams == null) {
            tempParams = ArrayMap<String, String>()
        }
        tempUrl = addParamsToUrl(tempUrl, tempParams)
        val requestBuilder = Request.Builder().url(tempUrl).get()
        if (tag != null) {
            if (tag is ArrayMap<*, *>) {
                throw RuntimeException()
            }
            requestBuilder.tag(tag.javaClass.name)
        }
        executeRequest(requestBuilder.build(), handler)
    }

    private fun addParamsToUrl(url: String, params: ArrayMap<String, String>?): String {
        if (params == null || params.size <= 0) {
            return url
        }
        val paramsBuilder = StringBuilder(url)
        paramsBuilder.append("?")
        for ((key, value) in params) {
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                LogUtils.w("An unexpected Null parameter appears in the request[GET $url] parameter:key=$key and value=$value")
                continue
            }
            paramsBuilder.append(key).append("=").append(value).append("&")
        }
        return paramsBuilder.substring(0, paramsBuilder.length - 1)
    }


    private fun checkUrlPrefix(url: String?): String {
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
            responseHandler.onFailed(request.url().toString(), -2, "The network connection is not available")
            return
        }
        val call = requestClient.newCall(request)
        executeCall(call, responseHandler)
    }

    private fun <T> executeCall(call: Call, responseHandler: ResponseHandler<T>) {
        val request = call.request()
        val reqUrl = request.url().toString()
        responseHandler.onStart()
        executor.execute {
            var response: Response? = null
            try {
                response = call.execute()
                if (response == null) {
                    resultCallbackHandler.post {
                        responseHandler.onFailed(reqUrl, -1, "Response Null")
                        responseHandler.onComplete()
                    }
                } else {
                    if (response.isSuccessful) {
                        val responseCode = response.code()
                        val bodyContent = response.body()!!.string()
                        responseHandler.ok = true
                        val apiResponse = jsonParse.fromJson(bodyContent, ApiResponse::class.java)
                        val data: T?
                        data = if (apiResponse?.result == null || apiResponse.result is JsonNull) {
                            null
                        } else {
                            jsonParse.fromJson(apiResponse.result, responseHandler.dataTypeClass)
                        }
                        resultCallbackHandler.post {
                            responseHandler.onSuccess(responseCode, apiResponse, data)
                            responseHandler.onComplete()
                        }
                    } else {
                        val errCode = response.code()
                        val errMsg = response.message()
                        resultCallbackHandler.post {
                            responseHandler.onFailed(reqUrl, errCode, errMsg)
                            responseHandler.onComplete()
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
                    responseHandler.onFailed(reqUrl, errorCode, errMsg)
                    responseHandler.onComplete()
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
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

            }

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
        private val lineSeparator = "\n"
        private val tab = "\t"

        private val requestName = Request::class.java.simpleName
        private val responseName = Response::class.java.simpleName
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
            logBuilder.append(lineSeparator)
            logBuilder.append(tab)
            logBuilder.append(requestMethod)
            logBuilder.append(tab)
            logBuilder.append(requestUrl)
            logBuilder.append(lineSeparator)

            logBuilder.append(responseName)
            logBuilder.append(lineSeparator)
            logBuilder.append(tab)
            logBuilder.append("code=").append(responseCode)
            logBuilder.append(tab)
            logBuilder.append("message=").append(responseMessage)
            logBuilder.append(tab)
            logBuilder.append("body=")
            logBuilder.append(lineSeparator).append(responseBody)
            LogUtils.d(logBuilder.toString())
            return response.newBuilder().body(ResponseBody.create(response.body()!!.contentType(), responseBody))
                    .build()
        }

        companion object {

            private val interceptor: LogInterceptor by lazy { LogInterceptor() }
            fun getInstance(): LogInterceptor = interceptor
        }
    }

    companion object {
        val API_HOST = String.format("https://www.baidu.com/")
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
