package com.zhuorui.securties.debug

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import base2app.ex.dp2px
import com.zhuorui.securties.debug.floatview.FloatViewProvider
import java.util.*


/**
 * Background
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  13:34
 */
@SuppressLint("StaticFieldLeak")
object Background {

    private val callBacks = HashSet<BackgroundCallback>()
    private var mActivityStopTimes = 0

    private var mTopActivity: Activity? = null

    private val mFloatViewProviders = mutableListOf<FloatViewProvider>()

    fun registerFloatViewProvider(floatViewProvider: FloatViewProvider) {
        mFloatViewProviders.add(floatViewProvider)
        mTopActivity?.let {
            floatViewProvider.attach(it.window.decorView as ViewGroup)
        }
    }

    fun unRegisterFloatViewProvider(floatViewProvider: FloatViewProvider) {
        mFloatViewProviders.remove(floatViewProvider).let {
            if (it) {
                mTopActivity?.let { act ->
                    floatViewProvider.detached(act.window.decorView as ViewGroup)
                }
            }
        }
    }

    fun registerBackgroundCallback(backgroundCallback: BackgroundCallback) {
        callBacks.add(backgroundCallback)
    }

    fun init(application: Application) {
        val activityCallbacks = object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            @SuppressLint("ClickableViewAccessibility")
            override fun onActivityStarted(activity: Activity) {
                val root = activity.findViewById<View>(android.R.id.content)
                if (root is FrameLayout && root.findViewById<View>(R.id.debug_view_id) == null) {
                    val debugView = ImageView(activity)
                    debugView.id = R.id.debug_view_id
                    debugView.setImageResource(R.drawable.debug_icon)
                    val params = FrameLayout.LayoutParams(
                        40.dp2px().toInt(),
                        40.dp2px().toInt()
                    )
                    params.gravity = Gravity.END
                    params.topMargin = (18.dp2px() * 10).toInt()
                    params.rightMargin = 18.dp2px().toInt()
                    debugView.layoutParams = params
                    root.addView(debugView)
                    debugView.setOnTouchListener(
                        FloatViewProvider.StackViewTouchListener(
                            debugView,
                            (18.dp2px() / 4).toInt()
                        )
                    )
                    debugView.setOnClickListener {
                        DebugConfigListProvider().attach(root)
                    }
                }

                mActivityStopTimes--
                if (mActivityStopTimes == -1) {
                    callBacks.forEach {
                        it.onBack2foreground()
                    }
                }
            }

            override fun onActivityResumed(activity: Activity) {
                mFloatViewProviders.forEach {
                    it.attach(activity.window.decorView as ViewGroup)
                }
                mTopActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                mFloatViewProviders.forEach {
                    it.detached(activity.window.decorView as ViewGroup)
                }
                mTopActivity = null
            }

            override fun onActivityStopped(activity: Activity) {

                mActivityStopTimes++
                if (mActivityStopTimes == 0) {
                    callBacks.forEach {
                        it.onFore2background()
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        }
        application.registerActivityLifecycleCallbacks(activityCallbacks)
    }

    /**
     * 判断应用是否后台运行
     */
    fun isBackgroundRunning(): Boolean {
        try {
            val processName = mTopActivity?.packageName
            val activityManager =
                mTopActivity?.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as? ActivityManager
                    ?: return false

            val processList = activityManager.runningAppProcesses ?: return false
            for (process in processList) {
                if (process.processName.equals(processName, ignoreCase = true)) {
                    return process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                            process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
                }
            }
        } catch (t: Throwable) {
        }
        return false
    }
}
