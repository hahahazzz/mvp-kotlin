package com.dmh.mvp.kotlin.module.home.view

import com.dmh.mvp.kotlin.R
import com.dmh.mvp.kotlin.base.BaseActivity
import com.dmh.mvp.kotlin.base.BaseContract
import com.dmh.mvp.kotlin.di.component.MainComponent
import com.dmh.mvp.kotlin.module.home.contract.SplashContract
import javax.inject.Inject

class SplashActivity : BaseActivity(), SplashContract.View {
    @Inject
    internal lateinit var presenter: SplashContract.Presenter

    override fun injectPresenter(component: MainComponent): BaseContract.Presenter<BaseContract.View> {
        component.inject(this)
        return presenter as BaseContract.Presenter<BaseContract.View>
    }

    override fun getLayoutResId(): Int = R.layout.activity_splash
}
