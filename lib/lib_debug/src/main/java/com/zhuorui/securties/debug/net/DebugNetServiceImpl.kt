package com.zhuorui.securties.debug.net

import base2app.network.DebugNetService
import com.alibaba.android.arouter.facade.annotation.Route
import com.zrlib.lib_service.base.BaseRouterPath
import okhttp3.Interceptor

/**
 * DebugNetServiceImpl
 *
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time 11:22
 */
@Route(path = BaseRouterPath.DEBUG_NET_SERVICE)
class DebugNetServiceImpl : DebugNetService() {

    /**
     * 退出登录回调
     */
    override fun onReceiveLoginOut() {
    }

    override fun provideNetIntercept(): Interceptor {
        return HttpDebugInterceptor()
    }

}