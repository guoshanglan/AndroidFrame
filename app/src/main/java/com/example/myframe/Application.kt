package com.example.myframe

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import base2app.BaseApplication
import base2app.util.AppUtil
import com.zhuorui.securties.skin.ZRSkinManager


/**
 * 主应用Application
 */
class Application : BaseApplication() {

    override val logTag: String
        get() = this.javaClass.simpleName


    override fun beforeInit() {
        // TODO 正式打包时需要注释以下代码
        // 集成本地异常捕获
        installCrashCanary()
    }


    override fun afterInit() {
        // 初始化换肤
        //initSkin()
    }

    override fun app2Foreground() {
        ZRSkinManager.instance.app2Foreground()
    }

    override fun app2Background() {
        ZRSkinManager.instance.app2Background()
    }

    /**
     * 主流程创建
     */
    override fun mainCreated() {
        // 加入App各个模块的推送管理

    }

    /**
     * 主流程结束
     */
    override fun mainDestroyed() {


    }

//    private fun initSkin() {
//        ZRSkinManager.instance.init(
//            this, SkinConfig.getInstance().mUIMode
//        ) { name, context, attrs -> ViewOpt.createView(name, context, attrs) }
//        ZRSkinManager.instance.updateAction = {
//            it.autoConvertDensity()
//            it
//        }
//        ZRSkinManager.instance.updateLocaleAction = {
//            it.autoCovertLocale()
//            it
//        }
//    }

    override fun getResources(): Resources {
        return ZRSkinManager.instance.mResources ?: super.getResources()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (AppUtil.isMainProcess(this)) {
            //保存系统的语言

            //处理横竖屏切换尺寸错误，导致显示错乱
            baseApplication.resources.autoConvertDensity()
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

    }
}