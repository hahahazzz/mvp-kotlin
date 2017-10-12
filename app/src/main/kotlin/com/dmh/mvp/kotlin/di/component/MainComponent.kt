package com.dmh.mvp.kotlin.di.component

import com.dmh.mvp.kotlin.di.module.ModelMappingModule
import com.dmh.mvp.kotlin.di.module.PresenterMappingModule
import com.dmh.mvp.kotlin.di.scope.PerActivity
import com.dmh.mvp.kotlin.di.scope.PerFragment
import com.dmh.mvp.kotlin.di.scope.PerPresenter
import com.dmh.mvp.kotlin.module.home.view.SplashActivity
import dagger.Component

/**
 * Created by qiugang on 2017/8/5.
 */
@PerActivity
@PerFragment
@PerPresenter
@Component(modules = arrayOf(PresenterMappingModule::class, ModelMappingModule::class))
interface MainComponent {
    fun inject(activity: SplashActivity)
}
