package com.dmh.mvp.kotlin.base

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import com.tencent.bugly.crashreport.CrashReport

class CrashReportService : Service() {
    companion object {
        val EXTRA_UNCAUGHT_EXCEPTION = "extraUncaughtException"
        private val REPORT_INTERVAL = 3 * 1000// 两次上报error的间隔,3s
        private val SERVICE_ALIVE_DURATION = 10 * 1000//服务空闲存活时长,10s
    }

    private val handler: Handler by lazy {
        object : Handler() {
            override fun handleMessage(msg: Message) {
                if (shouldStopService()) {
                    stopSelf()
                } else {
                    sendEmptyMessageDelayed(0, 1000)
                }
            }
        }
    }
    private var lastReportTimestamp: Long = 0   // 上一次上报时间的时间戳

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        lastReportTimestamp = currentTimestamp
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra(EXTRA_UNCAUGHT_EXCEPTION)) {
            val serializableExtra = intent.getSerializableExtra(EXTRA_UNCAUGHT_EXCEPTION)
            if (serializableExtra != null && serializableExtra is Throwable) {
                if (needReport()) {
                    lastReportTimestamp = currentTimestamp
                    CrashReport.postCatchedException(serializableExtra)
                }
            }
        }
        handler.sendEmptyMessage(0)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private val currentTimestamp: Long
        get() = System.currentTimeMillis()

    /**
     * 当前时间与上次上报时间间隔大于[CrashReportService.REPORT_INTERVAL],则上报,否则忽略
     * @return
     */
    private fun needReport(): Boolean {
        return System.currentTimeMillis() - lastReportTimestamp > REPORT_INTERVAL
    }

    /**
     * 当前时间与上次上报时间间隔大于[CrashReportService.SERVICE_ALIVE_DURATION],则停止掉服务
     * @return
     */
    private fun shouldStopService(): Boolean {
        return System.currentTimeMillis() - lastReportTimestamp >= SERVICE_ALIVE_DURATION
    }
}
