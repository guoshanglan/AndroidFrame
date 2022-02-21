package com.zrlib.lib_service.base

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author xuzuoliang
 * @email micrason@163.com
 * @date 2020/7/28 10:03
 * @desc 定义本项目ARouter接口服务访问内容提供者基类
 */
abstract class ServiceProvider : IProvider {

    private var context: Context? = null

    companion object {

        /**
         * 通知各模块当前已退出登录
         */
        fun notifyLoginOut() {
            serviceProviderList.forEach {
                it.onReceiveLoginOut()
            }
        }

        /**
         * 通知各模块当前已登录
         */
        fun notifyLogined() {
            serviceProviderList.forEach {
                it.onReceiveLogined()
            }
        }

        private val serviceProviderList = mutableSetOf<ServiceProvider>()
    }

    override fun init(context: Context?) {
        this.context = context
        serviceProviderList.add(this)
    }

    fun getContext(): Context {
        if (context == null) {
            throw NullPointerException("Service Provider Context is null !")
        }
        return context!!
    }

    /**
     * 已登录回调
     */
    protected open fun onReceiveLogined() {

    }

    /**
     * 已退出登录回调
     */
    protected open fun onReceiveLoginOut() {

    }
}