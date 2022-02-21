package base2app.ui.fragment

import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.DestinationFragment
import base2app.ui.activity.AbsActivity
import base2app.util.StatusBarUtil
import base2app.util.ToastUtil
import com.example.lib_base.R
import java.lang.ref.WeakReference

/**
 * @date 2020/5/26 9:29
 * @desc 不需要view,presenter的Fragment
 *  isBackFinish() 是否处理回退事件 默认为false
 *  isSupportSwipeBack()是否支持侧滑返回 默认为true
 */
open class ZRFragment(layoutId: Int? = null, cache: Boolean? = null) :
    DestinationFragment(layoutId, cache) {

    protected var TAG: String? = null

//    private var mDelegate: ZRSwipeBackFragmentDelegate? = null

    protected val topFragment: Fragment?
        get() {
            return activity?.let { if (it is AbsActivity) it.topFragment else null }
        }

    private var mDecorViewCacheMap = lazy { ArrayMap<String, WeakReference<View>>() }

    companion object {
        /**
         * 最外层window
         */
        private const val KEY_OUTER_WINDOW_CACHE = "out_window"

        /**
         * 最外层DecorView
         */
        private const val KEY_OUTER_DECOR_CACHE = "out_decor"

        /**
         * 第一层DecorView
         */
        private const val KEY_DECOR_CACHE = "decor"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = this.javaClass.simpleName
//        if (isSupportSwipeBack()) {
//            mDelegate = ZRSwipeBackFragmentDelegate(this)
//        }
    }

    override fun onDetach() {
        super.onDetach()
        if (mDecorViewCacheMap.isInitialized()) {
            mDecorViewCacheMap.value.clear()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)?.also {
            it.setTag(R.string.fragment_view, true)
            if (rootViewFitsSystemWindowsPadding() && it.paddingTop <= 0) {
                it.setPadding(0, getRootViewFitsSystemWindowsPadding(), 0, 0)
            }
        }
        return v
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mDelegate?.onViewCreated(view)
    }


    @CallSuper
    override fun onDestroyViewOnly() {
//        mDelegate?.doViewDestroy()
        super.onDestroyViewOnly()
    }

    /**
     * 视图是否减去状态栏的高度
     */
    open fun rootViewFitsSystemWindowsPadding(): Boolean {
        return false
    }

    protected open val parallaxOffset: Float
        get() = 0.5f


    /**
     * 设置视图减去状态栏的高度
     */
    open fun getRootViewFitsSystemWindowsPadding(): Int {
        return StatusBarUtil.getStatusBarHeight(context)
    }

    fun toast(@StringRes res: Int) {
        ToastUtil.instance.toastCenter(res)
    }

    fun toast(str: String?) {
        str?.let { ToastUtil.instance.toastCenter(it) }
    }

    /**
     * 是否支持侧滑返回
     */
    open fun isSupportSwipeBack(): Boolean {
        return parentFragment == null
    }

    /**
     * 是否处理回退事件
     */
    open fun isBackFinish(): Boolean {
        return false
    }

    /**
     * 查找DecorView(即fragment.view),
     * 由当前fragment.view开始向父视图查找标记tag为"window"的view,返回该view子视图中最近的fragment.view,
     * 如没有找到标记tag为"window"的view，则返回当前fragment.view
     * @param outer 默认false,ture:查找最后一层,false:查找第一层.
     */
    protected fun getDecorView(outer: Boolean? = null): View? {
        val curFragmentView = view ?: return null
        val isOuter = outer ?: false
        val cacheKey = if (isOuter) KEY_OUTER_DECOR_CACHE else KEY_DECOR_CACHE
        mDecorViewCacheMap.value[cacheKey]?.get()?.let {
            return it
        }
        val windowTag = getString(R.string.window_tag)
        var decorView: View? = null
        var fragmentView: View? = null
        var checkView: View? = curFragmentView
        do {
            checkView?.let { v ->
                if (v.getTag(R.string.fragment_view) == true) {
                    fragmentView = v
                }
                if (v.findViewWithTag<View>(windowTag) != null) {
                    decorView = fragmentView
                }
                checkView = v.parent?.let { if (it is View) it else null }
            }
        } while ((isOuter || decorView == null) && checkView != null)
        decorView = decorView ?: curFragmentView
        mDecorViewCacheMap.value[cacheKey] = WeakReference(decorView)
        return decorView
    }

    /**
     * 获取最外层Window
     * 由当前fragment view开始查找parent，
     * 找到最后一个标记tag为"window"的view，
     * 如没有找到标记tag为"window"的view,返回当前fragment view
     */
    protected fun getOuterWindow(): View? {
        val curFragmentView = view ?: return null
        mDecorViewCacheMap.value[KEY_OUTER_WINDOW_CACHE]?.get()?.let {
            return it
        }
        val windowTag = getString(R.string.window_tag)
        var windowView: View? = null
        var checkView: View? = curFragmentView
        do {
            checkView?.let { v ->
                v.findViewWithTag<View>(windowTag)?.let {
                    windowView = it
                }
                checkView = v.parent?.let { if (it is View) it else null }
            }
        } while (checkView != null)
        windowView = windowView ?: curFragmentView
        mDecorViewCacheMap.value[KEY_OUTER_WINDOW_CACHE] = WeakReference(windowView)
        return windowView
    }

    /**
     * 滑动返回开关
     */
    protected fun setSwipeBackEnableGesture(enable: Boolean) {
//        mDelegate?.setEnableGesture(enable)
    }

}