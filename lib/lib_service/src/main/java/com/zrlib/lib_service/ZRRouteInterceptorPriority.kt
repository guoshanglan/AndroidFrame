package com.zrlib.lib_service

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/10/28
 * dest : ZRRouteInterceptorPriority App 内部路由拦截跳转的优先级常量值
 * 相互保留一定间隔 , 防止后续扩展
 */
object ZRRouteInterceptorPriority {

    /**
     * 用于页面跳转拦截的Key值
     */
    const val INTERCEPTOR_KEY = "intercept"


    /**
     * 用户登录拦截优先级
     */
    const val USER_INTERCEPTOR_PRIORITY = 10

    /**
     * 预开户拦截优先级(即判断用户开户资料是否审核通过)
     */
    const val PRE_OPEN_ACCOUNT_INTERCEPTOR_PRIORITY = 20

    /**
     * 开户拦截优先级(即判断用户开户是否激活成功，openStatus是否为31)
     */
    const val OPEN_ACCOUNT_INTERCEPTOR_PRIORITY = 30

    /**
     * 强制设置交易密码拦截器(即判断交易用户是否设置过交易密码，如果未设置交易密码，则必须设置交易密码方可交易。)
     */
    const val FORCE_SET_TRANS_PASSWORD = 40
}