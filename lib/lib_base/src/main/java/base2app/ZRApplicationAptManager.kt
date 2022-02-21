package com.zhuorui.securities.base2app

import android.app.Application
import android.content.Context
import base2app.IAppInitProxy
import base2app.ex.logd


/**
 * ZRApplicationAptManager
 * @descraption
 * @time  9:59
 */
class ZRApplicationAptManager : IAppInitProxy {

    private val mIAppInitProxyList = mutableListOf<IAppInitProxy>()

    /**
     * gradle transform进行module application动态注册初始化的方法，不要删除
     */
    private fun registerInitProxy(className: String) {
        try {
            val appInitProxyClass = Class.forName(className)
            if (IAppInitProxy::class.java.isAssignableFrom(appInitProxyClass)) { //判断是不是一个接口
                val proxy = appInitProxyClass.getConstructor()
                    .newInstance() as IAppInitProxy
                mIAppInitProxyList.add(proxy)
            }
        } catch (e: Exception) {
            logd("registerInitProxy exception $e")
        }
    }

    override fun attachContext(context: Context?) {
        //gradle transform 动态注册插入的位置，如需调整，需要调整ZRApplicationTransform
        mIAppInitProxyList.forEach {
            println("${it::class.java.simpleName} attachContext")
            it.attachContext(context)
        }

    }

    override fun onAppCreate(application: Application) {
        mIAppInitProxyList.forEach {
            println("${it::class.java.simpleName} init")
            it.onAppCreate(application)
        }
    }

    override fun onAppBackground() {
        mIAppInitProxyList.forEach { it.onAppBackground() }
    }

    override fun onAppForeground() {
        mIAppInitProxyList.forEach { it.onAppForeground() }
    }

    override fun onAppClose() {
        mIAppInitProxyList.forEach { it.onAppClose() }
    }

    /**
     * 主流程创建
     */
    override fun mainCreated() {
        mIAppInitProxyList.forEach { it.mainCreated() }
    }

    /**
     * 主流程结束
     */
    override fun mainDestroyed() {
        mIAppInitProxyList.forEach { it.mainDestroyed() }
    }
}