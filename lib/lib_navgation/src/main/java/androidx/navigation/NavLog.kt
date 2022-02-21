package androidx.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/6/25 10:17
 *    desc   : 导航工具
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal object NavLog {

    /**
     * 打印主流程BackStack日志
     */
    fun logBackStack(activity: FragmentActivity) {
        if (!NavUtil.debug) return
        val fragmentHashCode = System.identityHashCode(activity)
        val tag =
            "${activity.javaClass.simpleName}[${Integer.toHexString(fragmentHashCode)}] main_backStack"
        NavUtil.findMainNavController(activity)
            .addOnDestinationChangedListener { controller, _, _ ->
                logBackStack(tag, controller)
            }
        val activityHashCode = activity.hashCode()
        val fragmentLifecycleCallbacks = object :
            FragmentManager.FragmentLifecycleCallbacks() {

            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
                if (!isFilterFragment(f)) {
                    Log.d(tag, " ")
                    Log.d(
                        tag,
                        "fragment：>>> resumedFragmentHashCode:${
                            Integer.toHexString(
                                System.identityHashCode(f)
                            )
                        }"
                    )
                    logFragments(tag, fm, "")
                    Log.d(tag, "fragment：-----------------------------")
                }
            }
        }
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
            fragmentLifecycleCallbacks,
            true
        )
        activity.application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (activityHashCode == System.identityHashCode(activity) && activity is FragmentActivity) {
                    Log.d(tag, "onActivityDestroyed: $activity")
                    activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(
                        fragmentLifecycleCallbacks
                    )
                    activity.application.unregisterActivityLifecycleCallbacks(this)
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

        })
    }

    /**
     * 打印子流程BackStack日志
     */
    fun logBackStack(fragment: Fragment) {
        if (!NavUtil.debug) return
        val fragmentHashCode = System.identityHashCode(fragment)
        val tag =
            "${fragment.javaClass.simpleName}[${Integer.toHexString(fragmentHashCode)}] sub_backStack"
        fragment.findNavController()
            .addOnDestinationChangedListener { controller, _, _ ->
                logBackStack(tag, controller)
            }
        val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
                if (!isFilterFragment(f)) {
                    Log.d(tag, " ")
                    Log.d(
                        tag,
                        "fragment：>>> resumedFragmentHashCode:${
                            Integer.toHexString(
                                System.identityHashCode(f)
                            )
                        }"
                    )
                    logFragments(tag, fm, "")
                    Log.d(tag, "fragment：-----------------------------")
                }
            }
        }
        fragment.childFragmentManager.registerFragmentLifecycleCallbacks(
            fragmentLifecycleCallbacks,
            false
        )
        fragment.parentFragment?.childFragmentManager?.registerFragmentLifecycleCallbacks(object :
            FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                super.onFragmentDestroyed(fm, f)
                if (System.identityHashCode(f) == fragmentHashCode) {
                    Log.d(tag, "onFragmentDestroyed: $f ${f.parentFragment}")
                    f.childFragmentManager.unregisterFragmentLifecycleCallbacks(
                        fragmentLifecycleCallbacks
                    )
                    f.parentFragment?.childFragmentManager?.unregisterFragmentLifecycleCallbacks(
                        this
                    )
                }
            }
        }, false)
    }

    @SuppressLint("RestrictedApi")
    private fun logBackStack(tag: String, it: NavController) {
        Log.d(tag, " ")
        Log.d(tag, "backStack：>>>")
        it.backStack.forEach {
            Log.d(tag, "backStack：- ${it.destination}")
        }
        Log.d(tag, "backStack：-----------------------------")
    }

    private fun logFragments(tag: String, fm: FragmentManager, sj: String) {
        fm.fragments?.forEach {
            if (it.javaClass.name != supportRequestManagerFragment) {
                if (it.childFragmentManager.fragments.isEmpty()) {
                    Log.d(tag, "fragment：$sj|- $it")
                } else {
                    Log.d(tag, "fragment：$sj- $it")
                    logFragments(
                        tag,
                        it.childFragmentManager,
                        "$sj    "
                    )
                }
            }
        }
    }

    private const val supportRequestManagerFragment =
        "com.bumptech.glide.manager.SupportRequestManagerFragment"

    private fun isFilterFragment(f: Fragment): Boolean {
        if (f.javaClass.name == supportRequestManagerFragment) return true
        f.childFragmentManager.fragments?.let {
            if (it.size > 1) {
                return true
            } else if (it.firstOrNull()?.javaClass?.name == supportRequestManagerFragment) {
                return false
            }
        }
        return false
    }

}