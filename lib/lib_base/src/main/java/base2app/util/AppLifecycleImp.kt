package base2app.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ProcessLifecycleOwner
import base2app.MainAct
import base2app.ZRActivityLifecycleCallbacks
import com.zhuorui.securities.base2app.ZRApplicationAptManager
import base2app.ex.logd
import com.zhuorui.securities.base2app.util.IAppLifecycle
import java.lang.ref.SoftReference
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by xieyingwu on 2017/12/22.
 * 监听Application内部AC的生命周期
 */
class AppLifecycleImp : IAppLifecycle, IMianLifecycle {

    private lateinit var mAppLifecycle: IAppLifecycle
    private lateinit var mMainLifecycle: IMianLifecycle

    /**
     * 当前活跃的Activity
     */
    private var mStartedActivity: Activity? = null

    /**
     * mStartedActivity stop后接力的SoftReference
     */
    private var mSRStartedActivity: SoftReference<Activity>? = null

    /**
     * 应用是否后台
     */
    private var mBackground = true

    /**
     * 总AC数量
     */
    private val appCreatedAcNum = AtomicInteger(0)

    /**
     * 主流程AC数量
     */
    private val mainCreatedAcNum = AtomicInteger(0)


    /**
     * 前后台AC数量
     */
    private val appFBCreatedAcNum = AtomicInteger(0)

    /**
     * 模块Application Manager
     */
    private val mZRApplicationAptManager = ZRApplicationAptManager()

    /**
     * 当前应用是否处于后台
     */
    fun isInBackground(): Boolean {
        return mBackground
    }

    /**
     * 获取当前显示的activity
     */
    fun getStartedActivity(): Activity? {
        return mStartedActivity ?: mSRStartedActivity?.get()
    }

    /**
     * 已启动的activity数量
     */
    fun getCreatedAcNum():Int{
        return appCreatedAcNum.get()
    }

    fun appCreate(application: Application) {
        mAppLifecycle = application as IAppLifecycle
        mMainLifecycle = application as IMianLifecycle
        mZRApplicationAptManager.onAppCreate(application)
        application.registerActivityLifecycleCallbacks(object : ZRActivityLifecycleCallbacks() {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                logd("$activity  onActivityCreated")
                if (mStartedActivity == null) {
                    mStartedActivity = activity
                }
                //某些手机下，热启动进入app时appFBCreatedAcNum就大于0，此时会导致后续得生命周期不回调。因此这里需要做一次重置。
                //有可能为某些原因MainActivity导致onActivityStopped不回调所致，具体缘由暂不详。
                if (appCreatedAcNum.get() == 0 && appFBCreatedAcNum.get() != 0) {
                    appFBCreatedAcNum.set(0)
                    logd("$activity  onActivityCreated reset appFBCreatedAcNum ${appFBCreatedAcNum.get()}")
                }
                appCreatedAcNum.incrementAndGet()
            }

            override fun onActivityStarted(activity: Activity) {
                logd("$activity  onActivityStarted")
                mStartedActivity = activity
                mSRStartedActivity?.clear()
                mSRStartedActivity = null
            }

            override fun onActivityResumed(activity: Activity) {
                super.onActivityResumed(activity)
                logd("$activity  onActivityResumed")
                //新开activity使用透明主题不会走onActivityStarted，所以在onActivityResumed更新一次
                mStartedActivity = activity
                if (appFBCreatedAcNum.incrementAndGet() == 1) {
                    app2Foreground()
                }
            }

            override fun onActivityStopped(activity: Activity) {
                super.onActivityStopped(activity)
                logd("$activity  onActivityStopped")
                if (activity == mStartedActivity) {
                    //用软引用接力，防止LeakCanary在ActivityDestroyed检测泄漏时误报mStartedActivity泄漏
                    mSRStartedActivity = SoftReference(activity)
                    mStartedActivity = null
                }
                if (appFBCreatedAcNum.decrementAndGet() == 0) {
                    app2Background()
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                logd("$activity  onActivityDestroyed")
                if (activity is MainAct) {
                    if (mainCreatedAcNum.decrementAndGet() == 0) {
                        mainDestroyed()
                    }
                }
                if (appCreatedAcNum.decrementAndGet() == 0) {
                    appClose()
                    mSRStartedActivity?.clear()
                    mSRStartedActivity = null
                }
            }

        })
    }

    override fun appAttachBaseContext(context: Context?) {
        mZRApplicationAptManager.attachContext(context)
        //APP前后台判断，ProcessLifecycleOwner内部维护逻辑
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun onMainActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (mainCreatedAcNum.incrementAndGet() == 1) {
            mainCreated()
        }
    }

    /**
     * qpp置于前台
     */
    override fun app2Foreground() {
        mBackground = false
        mAppLifecycle.app2Foreground()
        mZRApplicationAptManager.onAppForeground()
    }

    /**
     * app置于后台
     */
    override fun app2Background() {
        mBackground = true
        mZRApplicationAptManager.onAppBackground()
        mAppLifecycle.app2Background()
    }

    /**
     * app退出
     */
    override fun appClose() {
        mZRApplicationAptManager.onAppClose()
        mAppLifecycle.appClose()
    }

    /**
     * 主流程创建
     */
    override fun mainCreated() {
        mMainLifecycle.mainCreated()
        mZRApplicationAptManager.mainCreated()
    }

    /**
     * 主流程结束
     */
    override fun mainDestroyed() {
        mZRApplicationAptManager.mainDestroyed()
        mMainLifecycle.mainDestroyed()
    }

    fun appIsClose(): Boolean {
        return appCreatedAcNum.get() == 0
    }

}
