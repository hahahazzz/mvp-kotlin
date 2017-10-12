package com.dmh.mvp.kotlin.base

import com.dmh.mvp.kotlin.http.Api

/**
 * Created by QiuGang on 2017/9/27 11:26
 * Email : 1607868475@qq.com
 */
abstract class BaseModel : BaseContract.Model {
    protected val api: Api = Api.getApi()

    override fun destroy() {
        api.cancelRequest(this)
    }
}
