package com.zhuorui.securities.base2app.ui.fragment

import base2app.network.IScope
import base2app.network.ZRCoroutineScope
import base2app.network.cancelScope

/**
 * @date 2021/5/19 10:21
 * @desc kotlin协程网络请求基础presenter，不需要绑定EventBus
 */
open class ZRScopePresenter<V : ZRView> : ZRPresenter<V>(), IScope by ZRCoroutineScope() {

    override fun onDestory() {
        super.onDestory()
        cancelScope()
    }

}