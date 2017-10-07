package com.dmh.mvp.kotlin.base

import com.dmh.mvp.kotlin.utils.DebugUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by QiuGang on 2017/9/27 22:34
 * Email : 1607868475@qq.com
 */
abstract class BasePresenter<V : BaseContract.View> : BaseContract.Presenter<V> {
    init {
        EventBus.getDefault().register(this)
    }

    protected lateinit var view: V
    private var baseModel: BaseContract.Model? = null

    override fun attachView(v: V) {
        view = v
        baseModel = linkModel()
    }

    override fun linkModel(): BaseContract.Model? = null
    override fun start() {
    }

    override fun resume() {
        if (DebugUtils.debugConnection()) {
            view.close()
        }
    }

    override fun pause() {
        if (DebugUtils.debugConnection()) {
            view.close()
        }
    }

    override fun destroy() {
        baseModel?.destroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun defaultEventHandler(event: Any?) {

    }
}