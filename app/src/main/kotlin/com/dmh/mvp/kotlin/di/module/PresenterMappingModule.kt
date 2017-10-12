package com.dmh.mvp.kotlin.di.module

import com.dmh.mvp.kotlin.di.scope.PerActivity
import com.dmh.mvp.kotlin.module.home.contract.SplashContract
import com.dmh.mvp.kotlin.module.home.presenter.SplashPresenter
import dagger.Binds
import dagger.Module

/**
 * Created by qiugang on 2017/8/5 16:17
 * Email : 1607868475@qq.com
 */
@Module
abstract class PresenterMappingModule {
    @PerActivity
    @Binds
    abstract fun provideSplashPresenter(presenter: SplashPresenter): SplashContract.Presenter
}
