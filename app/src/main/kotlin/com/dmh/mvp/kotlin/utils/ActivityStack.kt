package com.dmh.mvp.kotlin.utils

import android.os.Process
import android.support.v7.app.AppCompatActivity
import java.util.*

/**
 * Created by QiuGang on 2017/9/27 22:35
 * Email : 1607868475@qq.com
 */
class ActivityStack private constructor() {
    private val activityStack: Stack<AppCompatActivity> by lazy { Stack<AppCompatActivity>() }

    companion object {
        private val instance: ActivityStack by lazy { ActivityStack() }
        fun get(): ActivityStack = instance
    }

    fun <T : AppCompatActivity> add(activity: T) = activityStack.add(activity)

    fun <T : AppCompatActivity> remove(activity: T) = activityStack.remove(activity)

    fun <T : AppCompatActivity> finish(cls: Class<T>) = activityStack.filter { it.javaClass == cls }.forEach { it.finish() }

    fun <T : AppCompatActivity> finishUntilEquals(cls: Class<T>) {
        while (activityStack.lastElement().javaClass != cls) {
            val lastElement = activityStack.lastElement()
            val remove = activityStack.remove(lastElement)
            if (remove) {
                lastElement.finish()
            }
        }
    }

    fun exit() {
        activityStack.map { it.finish() }
        Process.killProcess(Process.myPid())
        System.exit(0)
    }
}