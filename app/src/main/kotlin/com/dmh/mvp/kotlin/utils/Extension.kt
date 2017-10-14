package com.dmh.mvp.kotlin.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.view.WindowManager
import com.dmh.mvp.kotlin.base.App
import com.google.gson.Gson
import com.google.gson.JsonNull
import com.google.gson.JsonObject


/**
 * Created by QiuGang on 2017/10/3 22:31
 * Email : 1607868475@qq.com
 */
/****************资源文件扩展方法******************/
fun Context.dimen(@DimenRes id: Int) = this.resources.getDimensionPixelSize(id)

fun Context.color(@ColorRes id: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.resources.getColor(id, this.theme)
    } else {
        this.resources.getColor(id)
    }
}

fun Context.drawable(@DrawableRes id: Int): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        this.resources.getDrawable(id, this.theme)
    } else {
        this.resources.getDrawable(id)
    }
}

fun Context.string(@StringRes res: Int, vararg args: Any): String {
    return this.resources.getString(res, args)
}

/****************Activity相关扩展方法******************/
fun <T : Activity> T.drawStatusBarColor(@ColorRes color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color(color)
    }
}

/****************Gson相关扩展方法******************/
inline fun <reified T> Gson.fromJson(json: String): T {
    return this.fromJson(json, T::class.java)
}

fun JsonObject.getString(key: String): String {
    if (this.has(key) && (this.get(key) !is JsonNull)) {
        return this.get(key).asString
    }
    return ""
}

fun JsonObject.getInt(key: String): Int {
    if (this.has(key)) {
        return this.get(key).asInt
    }
    return 0
}

/****************标准库相关扩展方法******************/
fun Boolean?.judge(yes: (Boolean?) -> Unit, no: (Boolean?) -> Unit = {}): Boolean? {
    if (this == true) {
        yes(this)
    } else {
        no(this)
    }
    return this
}

fun Any?.ld() = LogUtils.d(this?.toString() ?: "Target is Null")

fun Any?.lw() = LogUtils.w(this?.toString() ?: "Target is Null")

fun Any?.le() = LogUtils.e(this?.toString() ?: "Target is Null")