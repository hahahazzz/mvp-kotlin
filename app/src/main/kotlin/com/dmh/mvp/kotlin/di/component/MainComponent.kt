package com.dmh.mvp.kotlin.di.component

import com.dmh.mvp.kotlin.SplashActivity
import com.dmh.mvp.kotlin.di.ModelMappingModule
import com.dmh.mvp.kotlin.di.module.PresenterMappingModule
import com.dmh.mvp.kotlin.di.scope.PerActivity
import com.dmh.mvp.kotlin.di.scope.PerFragment
import com.dmh.mvp.kotlin.di.scope.PerPresenter
import dagger.Component

@PerActivity
@PerFragment
@PerPresenter
@Component(modules = arrayOf(PresenterMappingModule::class, ModelMappingModule::class))
interface MainComponent {
    fun inject(activity: SplashActivity)
}
