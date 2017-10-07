package com.dmh.mvp.kotlin.utils

import android.os.Process
import android.support.v7.app.AppCompatActivity
import java.util.*

/**
 * Created by QiuGang on 2017/9/27 22:35
 * Email : 1607868475@qq.com
 */
class ActivityStack private constructor() {
    private val activityList by lazy { Stack<AppCompatActivity>() }

    companion object {
        private val INSTANCE: ActivityStack by lazy { ActivityStack() }
        fun get() = INSTANCE
    }

    fun add(activity: AppCompatActivity) = activityList.add(activity)

    fun remove(activity: AppCompatActivity) = activityList.remove(activity)

    fun remove(cls: Class<AppCompatActivity>) = activityList.removeAll { it.javaClass == cls }

    fun finishUntil(cls: Class<AppCompatActivity>) {
        if (activityList.size <= 0) {
            return
        }
        while (activityList.lastElement().javaClass != cls) {
            activityList.lastElement().finish()
        }
    }

    fun finish(cls: Class<AppCompatActivity>) = activityList.filter { it.javaClass == cls }.map { it.finish() }

    fun exit() {
        activityList.map { it.finish() }
        Process.killProcess(Process.myPid())
        System.exit(0)
    }
}