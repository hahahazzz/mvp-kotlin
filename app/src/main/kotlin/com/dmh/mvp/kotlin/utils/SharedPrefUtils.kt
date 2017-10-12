package com.dmh.mvp.kotlin.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager

import com.dmh.mvp.kotlin.base.App

/**
 * @Author : QiuGang
 * *
 * @Email : 1607868475@qq.com
 * *
 * @Date : 2017/7/6 17:21
 */
object SharedPrefUtils {
    private val SHARED_PREFERENCES = PreferenceManager.getDefaultSharedPreferences(App.get())

    fun read(key: String, defValue: String = ""): String {
        return SHARED_PREFERENCES.getString(key, defValue)
    }

    fun read(key: String, defValue: Boolean = false): Boolean {
        return SHARED_PREFERENCES.getBoolean(key, defValue)
    }

    fun read(key: String, defValue: Int = -1): Int {
        return SHARED_PREFERENCES.getInt(key, defValue)
    }

    fun read(key: String, defValue: Long = -1): Long {
        return SHARED_PREFERENCES.getLong(key, defValue)
    }

    fun save(key: String, value: String, apply: Boolean = true) {
        val editor = SHARED_PREFERENCES.edit().putString(key, value)
        save(editor, apply)
    }

    fun save(key: String, value: Boolean, apply: Boolean = true) {
        val editor = SHARED_PREFERENCES.edit().putBoolean(key, value)
        save(editor, apply)
    }

    fun save(key: String, value: Int, apply: Boolean = true) {
        val editor = SHARED_PREFERENCES.edit().putInt(key, value)
        save(editor, apply)
    }

    fun save(key: String, value: Long, apply: Boolean = true) {
        val editor = SHARED_PREFERENCES.edit().putLong(key, value)
        save(editor, apply)
    }

    fun remove(key: String) {
        val editor = SHARED_PREFERENCES.edit().remove(key)
        save(editor, false)
    }

    private fun save(editor: SharedPreferences.Editor, apply: Boolean = true) {
        if (apply) {
            editor.apply()
        } else {
            editor.commit()
        }
    }
}
