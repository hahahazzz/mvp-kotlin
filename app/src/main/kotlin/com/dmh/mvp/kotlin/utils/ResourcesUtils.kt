package com.dmh.mvp.kotlin.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.view.View

/**
 * @Author : QiuGang
 * *
 * @Email : 1607868475@qq.com
 * *
 * @Date : 2017/7/7 12:47
 */
object ResourcesUtils {
    fun getString(context: Context, @StringRes resId: Int, vararg args: Any): String {
        return context.resources.getString(resId, *args)
    }

    fun getColor(context: Context, @ColorRes resId: Int): Int {
        return ContextCompat.getColor(context, resId)
    }

    fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable {
        return ContextCompat.getDrawable(context, resId)
    }

    fun getDimen(context: Context, @DimenRes resId: Int): Int {
        return context.resources.getDimensionPixelSize(resId)
    }

    fun setBackground(view: View, @DrawableRes res: Int) {
        val drawable = getDrawable(view.context, res)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable)
        } else {
            view.background = drawable
        }
    }
}
