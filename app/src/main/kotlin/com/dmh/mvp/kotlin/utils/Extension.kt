package com.dmh.mvp.kotlin.utils

/**
 * Created by QiuGang on 2017/10/7 22:29
 * Email : 1607868475@qq.com
 */
fun String?.empty(): Boolean = (this == null || this.trim() == "")