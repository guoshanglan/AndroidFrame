package com.zrlib.lib_service.test


import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.zrlib.lib_service.ZRRouteInterceptorPriority
import com.zrlib.lib_service.route.QUREY
import com.zrlib.lib_service.route.ROUTE
import com.zrlib.lib_service.route.Voucher
import com.zrlib.lib_service.test.TestRouterPath.Companion.TestFragmentPath

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/11/5
 * dest : QuoteRouter
 */
interface TestRouter {

    /**
     * 跳转测试页面
     */
    @ROUTE(TestFragmentPath)
    fun toCommunityAssistant(@QUREY("data") classify: String? = null): Voucher



}