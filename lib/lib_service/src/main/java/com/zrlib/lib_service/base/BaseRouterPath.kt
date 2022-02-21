package com.zrlib.lib_service.base

/**
 * @author xuzuoliang
 * @email micrason@163.com
 * @date 2020/4/16 18:00
 * @desc 通用性路由零时的路由地址
 */
class BaseRouterPath {

    /**
     * 定义ARouter名称（规则：/模块名称/fragment or /activity类名）
     */
    companion object {

        /**
         * 主页
         */
        const val MAIN_FRAGMENT_PATH = "/app/fragment/mainFragment"

        /**
         * 通用横屏 Activity 的路由标识
         */
        const val BASE_LANDSCAPE_ACTIVITY = "/base/activity/AbsLandscapeActivity"


        /**
         * 通用横屏 Fragment 的路由标识
         */
        const val BASE_LANDSCAPE_FRAGMENT = "/base/fragment/ZRLandscapeFragment"

        /**
         * 通用独立竖屏 Activity 的路由标识
         */
        const val BASE_SINGLE_PORTRAIT_ACTIVITY = "/base/activity/AbsSinglePortraitActivity"

        /**
         * 通用webView页面
         */
        const val COMMON_WEB_PAGE = "/common/fragment/ZRWebViewFragment"


        /**
         * 网络拦截器调试服务
         */
        const val DEBUG_NET_SERVICE = "/zrdebug/expose/DebugNetServiceImpl"

    }

}