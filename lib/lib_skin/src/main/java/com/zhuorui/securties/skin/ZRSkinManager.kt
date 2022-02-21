package com.zhuorui.securties.skin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Process
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.NightMode
import androidx.core.view.LayoutInflaterCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.zhuorui.securties.skin.app.IViewCreator
import com.zhuorui.securties.skin.app.ZRLayoutInflaterFactory
import com.zhuorui.securties.skin.util.SkinUtil
import com.zhuorui.securties.skin.util.Utils
import com.zhuorui.securties.skin.view.ZRCustomView
import com.zhuorui.securties.skin.view.ZRSkinAble
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.system.exitProcess


/**
 * ZRSkinManager
 * @description
 * @date 2020/12/11 10:27
 */
class ZRSkinManager private constructor() :
    FragmentManager.FragmentLifecycleCallbacks(), ComponentCallbacks {

    private var mApplication: Application? = null

    private var mApplicationLayoutInflaterFactory: ZRLayoutInflaterFactory? = null
    private val mLayoutInflaterFactorys: MutableMap<Activity, ZRLayoutInflaterFactory> = ConcurrentHashMap()

    var mResources: Resources? = null

    private val mCustomViewMap: MutableMap<Any, CopyOnWriteArrayList<ZRSkinAble>?> = ConcurrentHashMap()

    @AppCompatDelegate.NightMode
    var mUIMode: Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    /**
     * 系统默认的 UIMODE
     */
    var mSysUimode: Int = AppCompatDelegate.MODE_NIGHT_YES

    /**
     * 系统是否有变动
     */
    var isSystemChange = false

    var isForeground = false

    private var iViewCreator: IViewCreator? = null

    private object SingletonHolder {
        val instance = ZRSkinManager()
    }

    companion object {
        val instance: ZRSkinManager
            get() = SingletonHolder.instance

        /**
         * 日夜模切换动画变更时长
         */
        const val SKIN_ANIMATION_INTERVAL: Long = 800
    }


    fun init(
        application: Application,
        @AppCompatDelegate.NightMode mode: Int,
        iViewCreator: IViewCreator? = null
    ) {
        this.iViewCreator = iViewCreator
        mApplication = application
        mResources = application.resources
        //这里必须使用mResources的逻辑去判断 因为 AppCompatDelegate.getDefaultNightMode() 默认一定是跟随
        mSysUimode = SkinUtil.getAPPUIMode(Resources.getSystem().configuration)
//        Log.d("mSysUimode", "mSysUimode : $mSysUimode ")

        val appTheme: Int = Utils.getManifestApplicationTheme(application)
        if (appTheme != 0) {
            application.theme.applyStyle(appTheme, true)
        }

        try {
            val layoutInflater = LayoutInflater.from(application)
            mApplicationLayoutInflaterFactory = ZRLayoutInflaterFactory(application, iViewCreator)
            LayoutInflaterCompat.setFactory2(layoutInflater, mApplicationLayoutInflaterFactory!!)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        application.registerComponentCallbacks(this)

        setDefaultUiMode(getRealMode(mode))
    }

    private fun setDefaultUiMode(@NightMode mode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(mode) // 设置默认的日夜间模式
        return Utils.updateUiModeForApplication(mApplication!!, mode)
    }


    /**
     * 提供给外部进行换肤的方法
     * isNightMode 代表是否是夜模
     * isShowAnimation 代表切换动画
     */
    @SuppressLint("WrongConstant")
    fun applySkin(@AppCompatDelegate.NightMode mode: Int, isShowAnimation: Boolean = false) {
        val useMode = getRealMode(mode)
        mApplication?.let {
            val uiModeChange: Boolean = setDefaultUiMode(useMode)
            var appTheme = 0
            // 应用Application
            if (uiModeChange) {
                appTheme = Utils.getManifestApplicationTheme(it)
                if (appTheme != 0) {
                    it.theme.applyStyle(appTheme, true)
                }
            }
            mLayoutInflaterFactorys.asIterable().forEach { factory ->
                factory.value.mCompatDelegate?.setLocalNightMode(useMode)
                factory.key.let { act ->
                    if (uiModeChange) {
                        val theme = Utils.getManifestActivityTheme(act)
                        if (theme != 0) {
                            act.theme.applyStyle(theme, true)
                        } else if (appTheme != 0) {
                            act.theme.applyStyle(appTheme, true)
                        }
                    }
                }
            }
//            Log.d("ZRSkinManager", "isNight2222 ? ${isNight()}")
            if (isShowAnimation)
                showUpdateAnimation()
            applySkinUi()
        }
    }

    private fun getRealMode(@AppCompatDelegate.NightMode mode: Int): Int {
        this.mUIMode = mode
        var useMode = mode
        if (mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {//如果是跟随系统则 , 使用第一次或者系统变更后的值
            useMode = mSysUimode
        }
        return useMode
    }


    /**
     * 渲染皮肤资源
     */
    private fun applySkinUi() {
        mApplication?.let { it ->
            Utils.correctConfigUiMode(it)
            mResources = it.resources
            mResources?.let { res ->
                mApplicationLayoutInflaterFactory?.applyUiMode(res)
                mLayoutInflaterFactorys.values.asIterable().forEach {
                    it.applyUiMode(resources = res)
                }
                mCustomViewMap.asIterable().forEach { it ->
                    Log.d("applySkinUi", "" + it.key.hashCode() + "   size : ${it.value?.size}")
                    it.value?.asIterable()?.forEach {
                        it.applyUIMode(resources = res)
                    }
                }
            }
        }
    }

    fun registLayoutFactor(activity: Activity) {
        try {
            val factory = ZRLayoutInflaterFactory(activity, iViewCreator)
            mLayoutInflaterFactorys[activity] = factory
            LayoutInflaterCompat.setFactory2(
                activity.layoutInflater,
                factory
            )
            if (activity is FragmentActivity) {
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                    this,
                    false
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun unRegistLayoutFactor(activity: Activity) {
        mLayoutInflaterFactorys.remove(activity)
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(
                this
            )
        }
    }

    /**
     * 用于主动释放 frgament 资源
     */
    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        f.activity?.let {
//            Log.d("onFragmentDestroyed", "onFragmentDestroyed: ${f.javaClass.simpleName}")
            val factory = mLayoutInflaterFactorys[it]
            factory?.clearUselessView()
        }
        mApplicationLayoutInflaterFactory?.clearUselessView()
    }


    /**
     * 注册需要换肤的接口 ， 后续如果不需要绑定 Activity 可以考虑做单一绑定 ， 但是最好是要做释放的处理
     * @param activity 对应组件当前的 Activity
     * @param view 需要换肤的视图
     * @param skin 换肤回调
     */
    fun registSkin(activity: Activity?, view: View, skin: (Resources) -> Unit) {
        if (activity == null) return
        val factory = mLayoutInflaterFactorys[activity]
        factory?.registSkin(ZRCustomView(view, skin))
    }


    /**
     * 注册需要换肤的接口 , 与 对应对象的周期进行绑定
     * @param any 任意绑定的生命周期对象
     * @param view 需要换肤的视图
     * @param skin 换肤回调
     */
    fun registSkin(any: Any, view: View, skin: ((Resources) -> Unit)? = null) {
        if (mCustomViewMap[any] == null)
            mCustomViewMap[any] = CopyOnWriteArrayList()
        if (view is ZRSkinAble) {
            mCustomViewMap[any]?.add(ZRCustomView(view) {
                view.applyUIMode(it)
            })
        } else {
            if (skin == null) throw Exception("非 ZRSkinAble 请独立实现换肤扩展!!!")
            mCustomViewMap[any]?.add(ZRCustomView(view, skin))
        }
    }

    fun registSkin(any: Any, view: ZRSkinAble) {
        if (mCustomViewMap[any] == null)
            mCustomViewMap[any] = CopyOnWriteArrayList()
        mCustomViewMap[any]?.add(view)
        view.applyUIMode(mResources)
    }

    /**
     * 反注册自定义皮肤 , 需要自己手动调用
     */
    fun unregistSkin(any: Any) {
        mCustomViewMap.remove(any)?.clear()
    }

    /**
     * 显示页面夜模切换淡出动画 , 柔和过渡
     */
    private fun showUpdateAnimation() {
        if (mLayoutInflaterFactorys.isNullOrEmpty()) return
        val activity: Activity = mLayoutInflaterFactorys.keys.last()
        //也有可能是Application的启动场景
        val decorView: View = activity.window.decorView
        val cacheBitmap = getBitmapFromView(decorView)
        if (decorView is ViewGroup && cacheBitmap != null) {
            val view = View(activity)
            view.background = BitmapDrawable(activity.resources, cacheBitmap)
            val layoutParam = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            decorView.addView(view, layoutParam)
            val objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
            objectAnimator.duration = SKIN_ANIMATION_INTERVAL
            objectAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    decorView.removeView(view)
                }
            })
            objectAnimator.start()
        }
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val last = mSysUimode
        mSysUimode = SkinUtil.getAPPUIMode(Resources.getSystem().configuration)
//        Log.d("mSysUimode", "mSysUimode : $mSysUimode ")
        //仅当前默认配置为跟随系统时 , 才重启 app
        if (mUIMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM && last != mSysUimode) {
//            mLayoutInflaterFactorys.keys.last().recreate()
            if (isForeground) {
                restartApp()
            } else {
                isSystemChange = true
            }
        }
//        else{//否则将当前属性重新刷新一次 防止出问题
//            applySkin(mUIMode)
//        }
    }

    /**
     *
     */
    private fun restartApp() {
        mApplication?.let { app ->
            val intent = app.packageManager.getLaunchIntentForPackage(app.packageName)
            intent?.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            app.startActivity(intent)
            Process.killProcess(Process.myPid())
            exitProcess(1)
        }
    }

    fun app2Foreground() {
        isForeground = true
        if (isSystemChange) {
            restartApp()
        }
    }

    fun app2Background() {
        isForeground = false
    }

    override fun onLowMemory() {
    }

    /**
     * 用于全局判断是否是黑夜模式
     */
    fun isNight(): Boolean {
        return if (mApplication == null) {
            false
        } else {
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        }
    }

    var updateAction: ((Resources) -> Resources)? = null
    var updateLocaleAction: ((Resources) -> Resources)? = null

    /**
     * 该方法主要用于通知 resource 属性变更用得
     */
    fun updateResourse(res: Resources) {
        updateAction?.invoke(res)
        mResources?.let {
            updateAction?.invoke(it)
        }
    }

    /**
     * 该方法主要用于通知 resource Locale属性变更用的
     */
    fun updateLocale(res: Resources) {
        updateLocaleAction?.invoke(res)
    }

    fun clear() {
        mCustomViewMap.clear()
    }
}
