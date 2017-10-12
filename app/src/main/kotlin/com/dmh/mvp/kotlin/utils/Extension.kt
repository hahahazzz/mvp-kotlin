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
fun Int.asDimen(context: Context) = context.resources.getDimensionPixelSize(this)

fun Int.asColor(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.resources.getColor(this, App.get().theme)
    } else {
        context.resources.getColor(this)
    }
}

fun Activity.getString(@StringRes res: Int, vararg args: Any): String {
    return this.resources.getString(res, args)
}

/****************Activity相关扩展方法******************/
fun <T : Activity> T.drawStatusBarColor(@ColorRes color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color.asColor(this)
    }
}


/****************Gson相关扩展方法******************/
inline fun <reified T> Gson.fromJson(json: String): T {
    return this.fromJson(json, T::class.java)
}

fun JsonObject.getString(key: String): String {
    if (this.has(key) && this.get(key) !is JsonNull) {
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