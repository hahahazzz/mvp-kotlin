package com.dmh.mvp.kotlin.base

/**
 * @Author : QiuGang
 * *
 * @Email : 1607868475@qq.com
 * *
 * @Date : 2017/7/6 9:11
 */
interface BaseContract {
    interface View {
        fun showLoadDialog()

        fun cancelLoadDialog()

        fun close()
    }

    interface Presenter<in V : View> {
        fun attachView(v: V)

        fun linkModel(): Model?

        fun start()

        fun resume()

        fun pause()

        fun destroy()
    }

    interface Model {
        fun destroy()
    }
}
