package com.dmh.mvp.kotlin.base

import android.app.Application
import android.content.Intent
import android.os.StrictMode
import com.dmh.mvp.kotlin.BuildConfig
import com.dmh.mvp.kotlin.R
import com.dmh.mvp.kotlin.utils.DebugUtils
import com.dmh.mvp.kotlin.utils.ResourcesUtils
import com.tencent.bugly.crashreport.CrashReport

/**
 * Created by QiuGang on 2017/9/27 22:28
 * Email : 1607868475@qq.com
 */
class App : Application() {
    companion object {
        private lateinit var app: App
        fun get(): App = app
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        Thread.setDefaultUncaughtExceptionHandler(GlobalUncaughtExceptionHandler.get())
        StrictMode.enableDefaults()
        initBugly()
    }

    private fun initBugly() {
        val strategy = CrashReport.UserStrategy(this)
        strategy.setAppVersion(BuildConfig.VERSION_NAME).appPackageName = BuildConfig.APPLICATION_ID
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG)
        CrashReport.initCrashReport(this, ResourcesUtils.getString(this, R.string.bugly_key), true, strategy)
    }

    internal class GlobalUncaughtExceptionHandler private constructor() : Thread.UncaughtExceptionHandler {

        override fun uncaughtException(t: Thread, e: Throwable) {
            if (DebugUtils.debugMode()) {
                e.printStackTrace()
            } else {
                val application = App.get()
                val intent = Intent(application, CrashReportService::class.java)
                intent.putExtra(CrashReportService.EXTRA_UNCAUGHT_EXCEPTION, e)
                application.startService(intent)
            }
        }


        companion object {
            private val handler: GlobalUncaughtExceptionHandler by lazy { GlobalUncaughtExceptionHandler() }
            fun get() = handler
        }
    }
}