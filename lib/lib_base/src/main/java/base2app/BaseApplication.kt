package base2app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.multidex.MultiDexApplication
import androidx.navigation.navInit
import androidx.navigation.openNavLog
import base2app.config.Config
import com.alibaba.android.arouter.launcher.ARouter
import base2app.ex.loge
import base2app.infra.LogInfra
import base2app.rxbus.RxBus
import com.example.lib_base.BuildConfig
import base2app.infra.MMKVManager
import base2app.network.Network
import com.zhuorui.securities.base2app.tread.DispatcherBackedScheduler
import base2app.ui.activity.AbsActivity
import base2app.util.AppLifecycleImp
import base2app.util.AppUtil
import com.zhuorui.securities.base2app.util.IAppLifecycle
import base2app.util.IMianLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.Dispatchers
import me.jessyan.autosize.AutoSizeCompat
import me.jessyan.autosize.AutoSizeConfig
import java.util.*

/**
 * Create by xieyingwu on 2018/4/3.
 * 类描述：基础的Application类信息
 */
abstract class BaseApplication : MultiDexApplication(), IAppLifecycle, IMianLifecycle {

    companion object {

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            protected set

        val baseApplication: BaseApplication
            get() = context as BaseApplication

        @SuppressLint("StaticFieldLeak")
        var mGlobalDensity: Float? = null
            protected set

        /**
         * 统一的尺寸适配
         */
        fun Resources.autoConvertDensity() {
            if (mGlobalDensity != null && mGlobalDensity != this.displayMetrics.density) {
                val isLandscape = this.configuration.orientation == ORIENTATION_LANDSCAPE
                val windowManager = baseApplication.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val defaultDisplay = windowManager.defaultDisplay

                val dmReal = DisplayMetrics()
                defaultDisplay?.getRealMetrics(dmReal)
                if (isLandscape) {//部分机型横竖屏切换会导致实际物理尺寸获取异常 , 取App加载第一次的获取的物理尺寸进行匹配
                    AutoSizeConfig.getInstance().screenWidth = dmReal.heightPixels
                    AutoSizeConfig.getInstance().screenHeight = dmReal.widthPixels
                } else {
                    AutoSizeConfig.getInstance().screenWidth = dmReal.widthPixels
                    AutoSizeConfig.getInstance().screenHeight = dmReal.heightPixels
                }
                AutoSizeCompat.autoConvertDensity(
                    this,
                    375f,
                    AutoSizeConfig.getInstance().screenWidth < AutoSizeConfig.getInstance().screenHeight
                )//如果有自定义需求就用这个方法
            }
        }


    }

    /**
     * 获取默认的Log的Tag标签
     *
     * @return TAG
     */
    protected abstract val logTag: String

    /**
     * app配置
     */
    lateinit var config: Config

    /**
     * 是否处于后台
     */
    val isInBackground: Boolean
        get() = aai.isInBackground()

    /**
     * app是否已经关闭了
     */
    val appIsClose: Boolean
        get() = aai.appIsClose()

    /**
     * 返回当前显示的Activity
     *
     * @return
     */
    val topActivity: AbsActivity?
        get() = aai.getStartedActivity()?.let { if (it is AbsActivity) it else null }

    /**
     * 已启动的activity数量
     */
    val createdAcNum:Int
    get() = aai.getCreatedAcNum()

    /**
     * app前后台，业务模块初始化管理
     */
    private var aai = AppLifecycleImp()

    override fun onCreate() {
        super.onCreate()
        if (!AppUtil.isMainProcess(this)) {
            //非主线程，不初始化相关组件
            return
        }
        beforeInit()
        initNavigation()
        AppUtil.init(this)
        initRxBus()
        initAutoSize()
        initARouter()
        initNetwork()
        afterInit()
        aai.appCreate(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (!AppUtil.isMainProcess(this)) {
            //非主线程，不初始化相关组件
            return
        }
        context = this
        initInfra()
        aai.appAttachBaseContext(base)
    }

    private fun initNavigation() {
        navInit()
        if (BuildConfig.DEBUG) {
            openNavLog()
        }
    }

    private fun initRxBus() {
        //RxJava全局处理错误，onError不能处理的异常
        RxJavaPlugins.setErrorHandler { throwable ->
            if (BuildConfig.DEBUG && Network.isDebugNetWorkThrow(throwable)) {
                throw throwable
            } else {
                loge("RxJava", "RxJavaPlugins.setErrorHandler", throwable)
            }
        }
        RxJavaPlugins.setInitIoSchedulerHandler { DispatcherBackedScheduler(Dispatchers.IO) }
        RxJavaPlugins.setInitComputationSchedulerHandler { DispatcherBackedScheduler(Dispatchers.Default) }
        // 初始化RxBus
        RxBus.setMainScheduler(AndroidSchedulers.mainThread())
    }

    private fun initAutoSize() {
        // 支持适配fragment
//        AutoSizeConfig.getInstance().isCustomFragment = true
        // 是否需要支持多进程
//        AutoSize.initCompatMultiProcess(this)
        //使用设备的实际尺寸做适配
        AutoSizeConfig.getInstance().isUseDeviceSize = true
        AutoSizeConfig.getInstance().isBaseOnWidth = true
        //屏蔽系统字体大小对 AndroidAutoSize 的影响
        AutoSizeConfig.getInstance().isExcludeFontScale = true
        AutoSizeCompat.autoConvertDensityOfGlobal(resources)
        mGlobalDensity = resources.displayMetrics.density
    }

    private fun initARouter() {
        if (BuildConfig.DEBUG) {  // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()     // 打印日志
//            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this)// 尽可能早，推荐在Application中初始化
    }

    /**
     * 系统初始化前
     */
    protected abstract fun beforeInit()

    /**
     * 系统初始化后
     */
    protected abstract fun afterInit()

    /**
     * 初始化日志和MMKV存储配置以及Config
     */
    private fun initInfra() {
        MMKVManager.getInstance().init(this)

        // 注意：这以下代码必须在MMKV初始化之后
        config = Config(this)
        val logLevel = config.logLevel()
        val debug = config.isDebug
        if (debug != null && logLevel != null) {
            // 初始化日志
            LogInfra.init(logTag, debug, logLevel)
        }
    }

    /**
     * 初始化网络配置
     */
    private fun initNetwork() {
        config.apply {
            Network.initRetrofit(
                domain(),
                oss(),
                isDebug,
                writeTimeout().toLong(),
                readTimeout().toLong(),
                connectTimeout().toLong()
            )
        }
    }

    /**
     * 分发主流程activity创建事件
     */
    fun dispatchMainActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        aai.onMainActivityCreated(activity, savedInstanceState)
    }

    /**
     * 初始化crash canary
     */
    protected fun installCrashCanary() {
        if (BuildConfig.DEBUG) {
            try {
                val crashCanaryClass = Class.forName("com.zhuorui.securties.debug.crashcanary.CrashCanary")
                val method = crashCanaryClass.getMethod("install", Context::class.java)
                method.invoke(null, this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
