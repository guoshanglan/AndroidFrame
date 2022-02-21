package com.zrlib.lib_service.app

import com.zrlib.lib_service.base.ServiceProvider
import com.zrlib.lib_service.service

/**
 * @author xuzuoliang
 * @email micrason@163.com
 * @date 2021/8/27 15:23
 * @desc
 */

abstract class AppService : ServiceProvider() {

    companion object {
        fun instance(): AppService? {
            return service(AppRouterPath.APP_EXPOSE_PATH)
        }
    }

    abstract fun intentToMainTradeTab()

    abstract fun intentToMainCommunityTab(isOpenEditLong: Boolean)

    abstract fun actionLanguageChange()

    abstract fun actionDeviceOffline()

    abstract fun isAgreeAppAgreement(): Boolean

    abstract fun inOpenAccountTab(): Boolean

    abstract fun intentToMainMarketTab()
}