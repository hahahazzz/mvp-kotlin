package com.dmh.mvp.kotlin.utils

import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by QiuGang on 2017/9/30 10:24
 * Email : 1607868475@qq.com
 */
object ThreadPool {
    private val executor = ThreadPoolExecutor(0, Runtime.getRuntime()
            .availableProcessors() * 2, 60L, TimeUnit.SECONDS, SynchronousQueue<Runnable>())

    fun execute(runnable: Runnable) = executor.execute(runnable)

    fun shutdownNow(): List<Runnable> = executor.shutdownNow()
}