package base2app

import android.app.Application
import android.content.Context
import base2app.util.IMianLifecycle

/**
 * IAppInitProxy
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  17:46
 * 模块application初始化接口
 */
interface IAppInitProxy: IMianLifecycle {

    /**
     * attachContext 也会是手动进行触发
     */
    fun attachContext(context: Context?)

    fun onAppCreate(application: Application)

    fun onAppBackground()

    fun onAppForeground()

    /**
     * ProcessLifecycleOwner 不会发 onDestory 的消息 , 所以这里会是手动进行触发的
     */
    fun onAppClose()

}