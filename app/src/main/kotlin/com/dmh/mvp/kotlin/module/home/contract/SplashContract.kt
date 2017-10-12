package com.dmh.mvp.kotlin.module.home.contract

import com.dmh.mvp.kotlin.base.BaseContract

/**
 * Created by QiuGang on 2017/10/12 14:28
 * Email : 1607868475@qq.com
 */
interface SplashContract {
    interface View : BaseContract.View

    interface Presenter : BaseContract.Presenter<View>

    interface Model : BaseContract.Model
}