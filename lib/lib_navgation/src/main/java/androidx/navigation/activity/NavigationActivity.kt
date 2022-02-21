package androidx.navigation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.WindowInsetsFrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.debug.DebugStackDelegate
import androidx.debug.DebugStackDelegate.Companion.BUBBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.hideSoftInput
import androidx.navigation.NavLog
import androidx.navigation.NavUtil
import androidx.navigation.R
import androidx.navigation.SingleDestiantion
import androidx.navigation.fragment.MainHostFragment
import java.lang.ref.WeakReference

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/6/28 11:08
 *    desc   : 导航Activity
 */
abstract class NavigationActivity : AppCompatActivity(){

    private val mFragmentLifecycleCallbacks = MyFragmentLifecycleCallbacks()

    private var mDebugStackDelegate: DebugStackDelegate? = null

    val topFragment: Fragment?
        get() {
            supportFragmentManager.fragments.iterator().forEach {
                if (it is MainHostFragment) {
                    return it.childFragmentManager.fragments.lastOrNull()
                }
            }
            return null
        }

    /**
     * 内容视图ID
     */
    protected open fun getContentLayoutId(): Int {
        return 0
    }

    /**
     * fragment容器View ID
     */
    protected open fun getContainerId(): Int {
        return 0
    }

    /**
     * 获取开始目的地
     * @return calssName
     */
    protected open fun getStartDestination(): String? {
        return null
    }

    /**
     * 获取开始目的地的参数
     */
    protected open fun getStartDestinationArgs(): Bundle? {
        return null
    }


    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = getContentLayoutId()
        if (layout != 0) {
            setContentView(layout)
        }
        initNavHost()
        if (NavUtil.debug) {
            mDebugStackDelegate = DebugStackDelegate(this)
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, true)
        mDebugStackDelegate?.onCreate(BUBBLE)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDebugStackDelegate?.onPostCreate(BUBBLE)
    }

    @CallSuper
    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks)
        mDebugStackDelegate?.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 初始化navgaition Host
     */
    private fun initNavHost() {
        val startDestination = getStartDestination() ?: return
        val containerId = getContainerId().let {
            if (it == 0 || it == View.NO_ID) {
                val windowInsetsFrameLayout = WindowInsetsFrameLayout(this).apply {
                    id = R.id.nav_host_fragment_container
                }
                setContentView(windowInsetsFrameLayout)
                windowInsetsFrameLayout.id
            } else {
                it
            }
        }
        val name = MainHostFragment::class.java.name
        val fm = supportFragmentManager
        val existingFragment = fm.findFragmentByTag(name)
        if (existingFragment == null) {
            val finalHost: Fragment = fm.fragmentFactory.instantiate(classLoader, name).apply {
                arguments = MainHostFragment.createArguments(
                    startDestination,
                    getStartDestinationArgs()
                )
            }
            fm.beginTransaction()
                .setReorderingAllowed(true)
                .add(containerId, finalHost, name)
                .setPrimaryNavigationFragment(finalHost) // this is the equivalent to app:defaultNavHost="true"
                .commitNowAllowingStateLoss()
        }
    }

    class MyFragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentCreated(fm, f, savedInstanceState)
            if (NavUtil.debug && f is MainHostFragment) {
                NavLog.logBackStack(f.requireActivity())
            }
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            super.onFragmentStarted(fm, f)
            if (f is SingleDestiantion) {
                f.activity?.let {
                    val requestKey = SingleDestiantion.getRequestKey(f)
                    val wr = WeakReference(f as SingleDestiantion)
                    it.supportFragmentManager.setFragmentResultListener(
                        requestKey,
                        f
                    ) { _, result ->
                        wr.get()?.onNewArguments(result)
                    }
                }
            }
        }

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            super.onFragmentResumed(fm, f)
            if (NavUtil.hideSoftInputFragment != 0) {
                if (NavUtil.hideSoftInputFragment == NavUtil.showSoftInputFragment) {
                    f.hideSoftInput()
                }
            } else if (NavUtil.showSoftInputFragment == 0) {
                f.hideSoftInput()
            }
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            super.onFragmentPaused(fm, f)
            NavUtil.showSoftInputFragment.let {
                if (it != 0 && it == f.hashCode()) {
                    NavUtil.hideSoftInputFragment = it
                }
            }
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            super.onFragmentStopped(fm, f)
            if (f is SingleDestiantion) {
                f.activity?.let {
                    val requestKey = SingleDestiantion.getRequestKey(f)
                    it.supportFragmentManager.clearFragmentResultListener(requestKey)
                }
            }
        }

    }

}