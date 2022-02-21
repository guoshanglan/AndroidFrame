package base2app.ui.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.StringRes
import androidx.navigation.activity.NavigationActivity
import base2app.BaseApplication
import base2app.BaseApplication.Companion.autoConvertDensity
import base2app.MainAct
import base2app.ex.skin
import base2app.ex.unregistSkin
import base2app.util.AppUtil
import base2app.util.QuickClickUtil
import base2app.util.StatusBarUtil
import base2app.util.ToastUtil
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.internal.CustomAdapt
import java.util.*


/**
 * 类描述：抽象的AC基类
 */
abstract class AbsActivity : NavigationActivity(), QuickClickUtil.Callback, CustomAdapt {
    protected var TAG: String? = null
    private var inStopLife: Boolean = false

    /**
     * 获取当前Ac的布局的xml布局content的布局id
     *
     * @return id
     */
    protected abstract val acContentRootViewId: Int

    /**
     * 默认采用全屏展示
     *
     * @return true;展示全屏
     */
    protected open val isFullScreen: Boolean
        get() = true

    /**
     * ContentView的布局
     *
     * @return layout
     */
    protected abstract val layout: Int

    protected val isDestroy: Boolean
        get() = isFinishing || isDestroyed

    private var dispatchTouchEventListener: LinkedList<OnDispatchTouchEventListener>? = LinkedList()
    private var orientationChangedListener: LinkedList<OnOrientationChangedListener>? = LinkedList()
    private val sdkResultCallback: LinkedList<SDKActivityResultCallback> = LinkedList()

    override fun getContainerId(): Int {
        return acContentRootViewId
    }

    override fun getContentLayoutId(): Int {
        return layout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //通知主流程activity Create
        if (this is MainAct) {
            BaseApplication.baseApplication.dispatchMainActivityCreated(this, savedInstanceState)
        }
//        ZRSkinManager.instance.registLayoutFactor(this)
        if (isFullScreen) beFullScreen()
        super.onCreate(savedInstanceState)
        TAG = this.javaClass.simpleName
        if (needChangeHeight()) {
            try {
                val container = findViewById<View>(acContentRootViewId)
                /*若是8.0设备，则判断导航栏的问题*/
                val parent = container.parent
                if (parent is ViewGroup) {
                    val rootView = parent as View
                    listenerNavigationListener(rootView)
                }
                val lp = container.layoutParams
                lp.height = AppUtil.screenHeight
//                loge("lp.height = " + lp.height)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        skin {
            if (resources.configuration?.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                applyStatusBar()
            }
        }
    }

    open fun applyStatusBar() {
        if (statusBarLightMode()) {
            StatusBarUtil.StatusBarLightMode(this)
        } else {
            StatusBarUtil.StatusBarBrightnessMode(this)
        }
    }

    open fun adaterSutoSizeScreen(): Boolean {
        return true
    }

    override fun isBaseOnWidth(): Boolean {
        return AutoSizeConfig.getInstance().screenWidth < AutoSizeConfig.getInstance().screenHeight
    }

    override fun getSizeInDp(): Float {
        //横竖屏都以最小边 375dp 为基准
        return 375f
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (adaterSutoSizeScreen())
            super.getResources().autoConvertDensity()

    }

    override fun onResume() {
        inStopLife = false
        super.onResume()

    }

    override fun onStop() {
        inStopLife = true
        super.onStop()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        sdkResultCallback.forEach {
            if (it.dispatchResult(requestCode, resultCode, data)) return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 第三方SDK处理Activity回调消息callback
     */
    interface SDKActivityResultCallback {

        fun dispatchResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean
    }

    fun registerSDKActivityResultCallback(l: SDKActivityResultCallback) {
        this.sdkResultCallback.add(l)
    }

    fun unregisterSDKActivityResultCallback(l: SDKActivityResultCallback) {
        this.sdkResultCallback.remove(l)
    }

    private fun listenerNavigationListener(rootView: View?) {
        if (rootView == null) return
        val phoneRealScreenHeight = AppUtil.phoneRealScreenHeight
        val phoneScreenHeight = AppUtil.phoneScreenHeight
        if (phoneRealScreenHeight != phoneScreenHeight) {
            /*若实际屏幕与视图屏幕不对，则表明存在虚拟按键，可进行虚拟按键的显示隐藏监听*/
            rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                var rootViewHeight: Int = 0

                override fun onGlobalLayout() {
                    if (inStopLife) return /*stop状态不更新*/
                    val viewHeight = rootView.height
                    if (rootViewHeight != viewHeight) {
                        rootViewHeight = viewHeight
                        if (rootViewHeight == AppUtil.phoneRealScreenHeight || rootViewHeight > AppUtil.phoneScreenHeight) {
                            /*虚拟按键隐藏了*/
                            navigation(rootViewHeight)
                        } else {
                            /*虚拟按键显示了*/
                            navigation(AppUtil.phoneScreenHeight)
                        }
                    }
                }
            })
        }
    }

    private fun navigation(value: Int) {
        try {
            val container = findViewById<View>(acContentRootViewId)
            val lp = container.layoutParams
            val isPortrait =
                this.resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            if (isPortrait) {
                lp.height = value
            } else {
                return
                //                lp.width = value;
            }
//            loge("lp.value = $value")
            container.layoutParams = lp
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun beFullScreen() {
        //设置状态栏沉浸
        val window = window
        window.requestFeature(Window.FEATURE_NO_TITLE)
        if (checkFullDisplay()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                window.statusBarColor = Color.TRANSPARENT
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
            val localLayoutParams = getWindow().attributes
            localLayoutParams.flags =
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags
        }

        applyStatusBar()
    }

    fun setFullscreen(isShowStatusBar: Boolean, isShowNavigationBar: Boolean) {
        var uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (!isShowStatusBar) {
            uiOptions = uiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        if (!isShowNavigationBar) {
            uiOptions = uiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        window.decorView.systemUiVisibility = uiOptions
        //隐藏标题栏
        supportActionBar?.hide()
    }

    private fun checkFullDisplay(): Boolean {
        return false
    }

    /**
     * 状态栏图标文字是否变灰
     *
     * @return
     */
    protected open fun statusBarLightMode(): Boolean {
        return true
    }

    open fun needChangeHeight(): Boolean {
        return false
    }

    protected fun toast(@StringRes res: Int) {
        ToastUtil.instance.toast(res)
    }

    protected fun toast(str: String?) {
        str?.let { ToastUtil.instance.toast(it) }
    }

    override fun clickToFast() {

    }

    interface OnDispatchTouchEventListener {
        fun onTouch(ev: MotionEvent?)
    }

    interface OnOrientationChangedListener {
        fun onChange(landscape: Boolean)
    }

    fun addOrientationChangedListener(listener: OnOrientationChangedListener) {
        removeOrientationChangedListener(listener)
        this.orientationChangedListener?.add(listener)
    }

    fun removeOrientationChangedListener(listener: OnOrientationChangedListener) {
        this.orientationChangedListener?.remove(listener)
    }

    fun addDispatchTouchEventListener(listener: OnDispatchTouchEventListener) {
        removeDispatchTouchEventListener(listener)
        this.dispatchTouchEventListener?.add(listener)
    }

    fun removeDispatchTouchEventListener(listener: OnDispatchTouchEventListener) {
        this.dispatchTouchEventListener?.remove(listener)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        dispatchTouchEventListener?.forEach { it.onTouch(ev) }
        return super.dispatchTouchEvent(ev)
    }

    override fun getResources(): Resources {
        if (adaterSutoSizeScreen())
            super.getResources().autoConvertDensity()
        return super.getResources()
    }

    override fun onDestroy() {
        super.onDestroy()
//        ZRSkinManager.instance.unRegistLayoutFactor(this)
        unregistSkin()
        sdkResultCallback.clear()
    }

}