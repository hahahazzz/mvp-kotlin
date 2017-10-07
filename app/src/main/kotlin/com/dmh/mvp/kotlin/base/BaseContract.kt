package com.dmh.mvp.kotlin.base

/**
 * Created by QiuGang on 2017/9/27 22:32
 * Email : 1607868475@qq.com
 */
interface BaseContract {
    interface View {
        fun showLoadDialog()
        fun cancelLoadDialog()
        fun close()
    }

    interface Presenter<in V : BaseContract.View> {
        fun attachView(v: V)
        fun linkModel(): BaseContract.Model?
        fun start()
        fun resume()
        fun pause()
        fun destroy()
    }

    interface Model {
        fun destroy()
    }
}