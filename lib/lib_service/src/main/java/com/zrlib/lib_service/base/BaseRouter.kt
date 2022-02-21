package com.zrlib.lib_service.base

import com.zrlib.lib_service.base.BaseRouterPath.Companion.COMMON_WEB_PAGE
import com.zrlib.lib_service.route.QUREY
import com.zrlib.lib_service.route.ROUTE
import com.zrlib.lib_service.route.Voucher

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/11/5
 * dest : QuoteRouter
 */
interface BaseRouter {

    /**
     * 跳转网页
     */
    @ROUTE(COMMON_WEB_PAGE)
    fun toWeb(
        @QUREY("url") url: String?,
        @QUREY("title") title: String? = null,
        @QUREY("isTopBar") isTopBar: Boolean = true,
    ): Voucher

}