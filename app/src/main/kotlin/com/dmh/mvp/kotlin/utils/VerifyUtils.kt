package com.dmh.mvp.kotlin.utils

import java.util.regex.Pattern

/**
 * 验证的工具类
 */
object VerifyUtils {

    /**
     * 正则表达式：验证用户名
     */
    val REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$"

    /**
     * 正则表达式：验证密码
     */
    // public static final String REGEX_PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,18}$";
    //public static final String REGEX_PASSWORD = "(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,8}";
    val REGEX_PASSWORD = "^[^ ]{8,16}$"

    /**
     * 正则表达式：验证手机号
     */
    val REGEX_MOBILE = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$"

    /**
     * 正则表达式：验证邮箱
     */
    val REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)" + "+[a-zA-Z]{2,}$"

    /**
     * 正则表达式：验证汉字
     */
    val REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$"

    /**
     * 正则表达式：验证身份证
     */
    val REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)"

    /**
     * 正则表达式：验证URL
     */
    val REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w-] ./?%&=]*)?"

    /**
     * 正则表达式：验证IP地址
     */
    val REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)"
    /**
     * 正则表达式：验证昵称
     */
    private val REGEX_NAME = "^[\\u4e00-\\u9fa5]{4,8}$"

    /**
     * 校验用户名
     * @param username
     * *
     * @return 校验通过返回true，否则返回false
     */
    fun isUsername(username: String): Boolean {
        return Pattern.matches(REGEX_USERNAME, username)
    }

    /**
     * 校验密码
     * @param password
     * *
     * @return 校验通过返回true，否则返回false
     */
    fun isPassword(password: String): Boolean {
        return Pattern.matches(REGEX_PASSWORD, password)
    }

    /**
     * 校验昵称
     * @param name
     * *
     * @return 校验通过返回true，否则返回false
     */
    fun isName(name: String): Boolean {
        return Pattern.matches(REGEX_NAME, name)
    }

    /**
     * 检验是否是Url
     * @param url
     * *
     * @return 校验通过返回true，否则返回false
     */
    fun isUrl(url: String): Boolean {
        return Pattern.matches(REGEX_URL, url)
    }

    /**
     * 校验手机号
     * @param mobile
     * *
     * @return 校验通过返回true，否则返回false
     */
    fun isMobile(mobile: String): Boolean {
        return Pattern.matches(REGEX_MOBILE, mobile)
    }

    /**
     * 校验邮箱
     * @param email
     * *
     * @return 校验通过返回true，否则返回false
     */
    fun isEmail(email: String): Boolean {
        return Pattern.matches(REGEX_EMAIL, email)
    }

    /**
     * 校验汉字
     * @param chinese
     * *
     * @return 校验通过返回true，否则返回false
     */
    fun isChinese(chinese: String): Boolean {
        return Pattern.matches(REGEX_CHINESE, chinese)
    }

    /**
     * 校验IP地址
     * @param ipAddr
     * *
     * @return
     */
    fun isIPAddr(ipAddr: String): Boolean {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr)
    }

    /**
     * @param string 验证昵称
     * *
     * @return
     */
    fun isConSpeCharacters(string: String): Boolean {
        if (string.replace("[\u4e00-\u9fa5]*[a-z]*[A-Z]*\\d*-*_*\\s*".toRegex(), "").length == 0) {
            //不包含特殊字符
            return false
        }
        return true
    }

}
