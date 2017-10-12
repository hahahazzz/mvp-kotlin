package com.dmh.mvp.kotlin.base

import android.app.Application
import android.content.Intent
import android.os.StrictMode
import com.dmh.mvp.kotlin.BuildConfig
import com.dmh.mvp.kotlin.R
import com.dmh.mvp.kotlin.utils.DebugUtils
import com.tencent.bugly.crashreport.CrashReport

/**
 * 全局的参数设置
 */
class App : Application() {
    companion object {
        private lateinit var instance: App

        fun get(): App = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Thread.setDefaultUncaughtExceptionHandler(GlobalUncaughtExceptionHandler.get())
        StrictMode.enableDefaults()
        initUmeng()
        initBugly()
    }

    private fun initUmeng() {
        /*UMShareAPI.get(this)//友盟分享所需要的参数
        PlatformConfig.setWeixin(getString(R.string.wx_share_id), getString(R.string.wx_share_secret))
        PlatformConfig.setQQZone(getString(R.string.qq_share_id), getString(R.string.qq_share_secret))*/
    }

    private fun initBugly() {
        val strategy = CrashReport.UserStrategy(this)
        strategy.setAppVersion(BuildConfig.VERSION_NAME).appPackageName = BuildConfig.APPLICATION_ID
        CrashReport.setIsDevelopmentDevice(this, BuildConfig.DEBUG)
        CrashReport.initCrashReport(this, getString(R.string.bugly_key), true, strategy)
    }

    internal class GlobalUncaughtExceptionHandler private constructor() : Thread.UncaughtExceptionHandler {

        override fun uncaughtException(t: Thread, e: Throwable) {
            if (DebugUtils.debugMode()) {
                e.printStackTrace()
            } else {
                val application = instance
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
