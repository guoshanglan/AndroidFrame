package com.zrlib.lib_service.temp

import com.zrlib.lib_service.route.ROUTE
import com.zrlib.lib_service.route.Voucher

/**

@author guoshanglan
@description:临时模块路由凭证跳转
@date : 2021/7/29 14:08
 */
interface TempRouter {

    /**
     * 跳转直播页面
     */
    @ROUTE(TempRouterPath.LIVE_FRAGMENT)
    fun toLiveFragment(): Voucher


}