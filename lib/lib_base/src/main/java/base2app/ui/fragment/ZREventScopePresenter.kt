package base2app.ui.fragment

import base2app.network.IScope
import base2app.network.ZRCoroutineScope
import base2app.network.cancelScope
import com.zhuorui.securities.base2app.ui.fragment.ZRNetPresenter
import com.zhuorui.securities.base2app.ui.fragment.ZRView

/**
 * @author xuzuoliang
 * @email micrason@163.com
 * @date 2021/5/19 11:04
 * @desc kotlin协程网络请求基础presenter，需要绑定EventBus
 */
open class ZREventScopePresenter<V : ZRView> : ZRNetPresenter<V>(), IScope by ZRCoroutineScope() {

    override fun onDestory() {
        super.onDestory()
        cancelScope()
    }
}