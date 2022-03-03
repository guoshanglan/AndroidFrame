package com.zrlib.lib_service.home

import com.zrlib.lib_service.route.QUREY
import com.zrlib.lib_service.route.ROUTE
import com.zrlib.lib_service.route.Voucher

/**

@author: guoshanglan
@description:
@date : 2022/3/2 14:01
 */
interface HomeRouter {

    /**
     * 跳转二级页面 咋
     */
    @ROUTE(HomeRouterPath.SecondFragmentPath)
    fun toSecondFragment(@QUREY("path") path: String? = null): Voucher

    @ROUTE(HomeRouterPath.TestFragmentPath)
    fun toTestFragmentPath(): Voucher


}