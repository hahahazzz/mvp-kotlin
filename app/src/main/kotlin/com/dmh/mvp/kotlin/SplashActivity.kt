package com.dmh.mvp.kotlin

import com.dmh.mvp.kotlin.base.BaseActivity
import com.dmh.mvp.kotlin.base.BaseContract
import com.dmh.mvp.kotlin.di.component.MainComponent
import javax.inject.Inject

class SplashActivity : BaseActivity() {
    override fun injectPresenter(component: MainComponent): BaseContract.Presenter<BaseContract.View> {
        TODO("inject presenter")
    }

    override fun getLayoutResId(): Int = R.layout.activity_splash
}
