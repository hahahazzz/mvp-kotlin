package com.dmh.mvp.kotlin.module.home.presenter

import com.dmh.mvp.kotlin.base.BasePresenter
import com.dmh.mvp.kotlin.module.home.contract.SplashContract
import javax.inject.Inject

/**
 * Created by QiuGang on 2017/10/12 14:39
 * Email : 1607868475@qq.com
 */
class SplashPresenter @Inject
constructor() : BasePresenter<SplashContract.View>(), SplashContract.Presenter {

}