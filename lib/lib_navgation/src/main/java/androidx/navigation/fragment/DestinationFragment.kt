package androidx.navigation.fragment

import android.animation.Animator
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.fragment
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.fragment.app.result.*
import androidx.navigation.NavUtil

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/6/18 17:44
 *    desc   : 目的地Fragment
 */
abstract class DestinationFragment(
    @LayoutRes layoutId: Int? = null,
    cache: Boolean? = null
) : Fragment(layoutId ?: 0), IFragmentResultController by FragmentResultController(),
    AnimGenerator.AnimationListener {

    private val debug = NavUtil.debug

    /**
     * 动画生成器
     */
    private val mAnimGenerator = AnimGenerator()

    /**
     * 是否缓存View
     */
    private val mCache = cache ?: true

    /**
     * 缓存View
     */
    private var mCacheView: SaveHierarchyStateView? = null

    /**
     * 缓存View是否初始化
     */
    protected var cacheViewInit = false

    /**
     * 是否延迟加载过
     */
    private var mLazyInit = false

    /**
     * 上次延迟可见时记录的可见次数
     */
    private var mLazyLastResumeN = -1

    /**
     * fragment可见次数
     */
    private var mResumeN = -1

    /**
     * 是否开启过滤LeakCanary获取View
     */
    private var mOpenFilterLeakCanary = false

    /**
     * 是否给过子Fragment进场动画状态
     */
    private var mChildRequireAnim = false

    /**
     * 动画结束后额外延迟时间，优化延迟加载view衔接时会卡顿最后一刻动画
     */
    private val lazyDelayMillis = 16L

    /**
     * 懒加载Runnable
     */
    private val lazyRun = Runnable {
        if (isResumed) {
            if (!mLazyInit) {
                mLazyInit = true
                onViewCreatedLazy()
            }
            if (mLazyLastResumeN != mResumeN) {
                mLazyLastResumeN = mResumeN
                onResumeLazy(mResumeN)
            }
        }
    }

    fun getResumeN(): Int {
        return mResumeN
    }

    override fun getView(): View? {
        val v = super.getView() ?: mCacheView
        //通过view返回null,禁用LeakCanary在onDestoryView检测泄漏功能，在onDestoryView会存在误报
        return if (isFormLeakCanary()) null else v
    }

    /**
     * 是否LeakCanary调用
     */
    private fun isFormLeakCanary(): Boolean {
        if (debug && mOpenFilterLeakCanary) {
            val stack = Throwable().stackTrace
            if (stack.size > 2) {
                val s = stack[2]
                if (s.fileName == "AndroidXFragmentDestroyWatcher.kt") {
                    return true
                }
            }
        }
        return false
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (debug) {
            Log.d("life", "$this onSaveInstanceState: ")
        }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (debug) {
            Log.d("life", "$this onCreate")
        }
        mAnimGenerator.bindFragment(this)
        fragmentResultBindFragment(this, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (debug) {
            Log.d("life", "$this onCreateView:")
        }
        mOpenFilterLeakCanary = false
        //不缓存view，上一次使用的view还没有回收，回收上一次view
        if (!mCache && mCacheView != null) {
            viewDestroy()
        }
        val view = mCacheView ?: SaveHierarchyStateView(this).also { root ->
            root.id = androidx.navigation.R.id.destination_root_view
            super.onCreateView(inflater, root, savedInstanceState)?.let { content ->
                content.background?.let { bg ->
                    root.background = bg
                    content.background = null
                }
                root.addView(
                    content,
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )
            }
            mCacheView = root
        }
        //被系统回收重新构建，或不缓存view，启用view状态恢复
        view.restoreHierarchyState = !mCache || savedInstanceState != null
        /*
        * 在container不为空时清除View的依赖（container为空时不能清除），
        * 否则在FragmentStateManager.addViewToCaontainer(FragmentStateManager.java:830),
        * 添加时会出现(IllegalStateException:The specified child already has a parent,...)异常
        */
        if (container != null) {
            view.parent?.let { parent ->
                if (parent is ViewGroup) {
                    parent.removeView(view)
                }
            }
        }
        return view
    }


    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (debug) {
            Log.d("life", "$this onViewCreated: ")
        }
        if (!cacheViewInit) {
            cacheViewInit = true
            onViewCreatedOnly(view, savedInstanceState)
        }
    }

    /**
     * View创建，在Fragment生命周期中mView实例发生变化执行一次。在onViewCreated()方法中执行
     */
    protected open fun onViewCreatedOnly(view: View, savedInstanceState: Bundle?) {
        if (debug) {
            Log.d("life", "$this onViewCreatedOnly: ")
        }
    }

    override fun onStart() {
        super.onStart()
        if (debug) {
            Log.d("life", "$this onStart: ")
        }
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        if (mCache && mCacheView == null) {
            throw RuntimeException("cache == $mCache,check super.onCreateView(inflater, container, savedInstanceState) or use DestinationFragment(id,false)")
        }
        mResumeN++
        if (debug) {
            Log.d("life", "$this onResume: resumeN:$mResumeN")
        }
    }

    /**
     * 延迟创建View，在Fragment生命周期中mView实例发生变化执行一次，在onResume后，onResumeLazy前执行
     */
    protected open fun onViewCreatedLazy() {
        if (debug) {
            Log.d("life", "$this onViewLazyCreated: ")
        }
    }


    /**
     * 延迟Resume，在进场动画结束后调用，并在onResume后执行
     */
    protected open fun onResumeLazy(n: Int) {
        if (debug) {
            Log.d("life", "$this onLazyResume: ")
        }
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        if (debug) {
            Log.d("life", "$this onPause: ")
        }
        view?.removeCallbacks(lazyRun)
    }

    override fun onStop() {
        super.onStop()
        if (debug) {
            Log.d("life", "$this onStop: ")
        }
    }

    @CallSuper
    override fun onDestroyView() {
        if (debug) {
            Log.d("life", "$this onDestroyView: ")
        }
        mOpenFilterLeakCanary = true
        if (!mCache) {
            viewDestroy()
        }
        super.onDestroyView()
    }

    /**
     * 回收View,在Fragment生命周期中mView实例发生变化执行一次。会在fragment回收时执行当前view，或更新view时执行上一次view回收
     */
    protected open fun onDestroyViewOnly() {
        if (debug) {
            Log.d("life", "$this onDestroyViewOnly: ")
        }
    }

    @CallSuper
    override fun onDestroy() {
        mCacheView?.let {
            viewDestroy()
        }
        if (debug) {
            Log.d("life", "$this onDestroy: ")
        }
        mOpenFilterLeakCanary = false
        super.onDestroy()
    }

    private fun viewDestroy() {
        onDestroyViewOnly()
        mCacheView?.let { v ->
            v.parent?.let { parent ->
                if (parent is ViewGroup) {
                    parent.removeView(v)
                }
            }
        }
        mCacheView = null
        cacheViewInit = false
        mLazyInit = false
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return mAnimGenerator.createAnimation(transit, enter, nextAnim)
            ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        return mAnimGenerator.createAnimator(transit, enter, nextAnim)
            ?: super.onCreateAnimator(transit, enter, nextAnim)
    }

    protected fun isLazyInit(): Boolean {
        return mLazyInit
    }

    /**
     * 进场动画开始
     */
    final override fun onEnterAnimStart() {
        dispatchEnterAniming()
    }

    /**
     * 进场动画结束
     */
    final override fun onEnterAnimEnd() {
        dispatchEnterAnimEnd()
    }

    /**
     * 是否有进场动画
     */
    final override fun isEnterAnim(): Boolean {
        return mAnimGenerator.isRunging().also {
            if (it) {
                mChildRequireAnim = true
            }
        }
    }

    /**
     * 向子类通知进场动画进行状态
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal fun dispatchEnterAniming() {
        mAnimGenerator.setRunging(true)
        childFragmentManager.fragments.forEach {
            if (it is DestinationFragment && it.isResumed) {
                mChildRequireAnim = true
                it.dispatchEnterAniming()
            }
        }
    }

    /**
     * 向子类通知进场动画结束状态
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    internal fun dispatchEnterAnimEnd() {
        mAnimGenerator.setRunging(false)
        view?.postDelayed(lazyRun, lazyDelayMillis)
        if (mChildRequireAnim) {
            mChildRequireAnim = false
            childFragmentManager.fragments.forEach {
                if (it is DestinationFragment && it.isResumed) {
                    it.dispatchEnterAnimEnd()
                }
            }
        }
    }

    /**
     * 注册Fragment消息监听
     */
    fun <I, O : Any> registerForFragmentResult(
        contract: FragmentResultContract<I, O>,
        callBack: FragmentResultCallback<O>
    ): FragmentResultLauncher<I> {
        return registerForFragmentResult(this, contract, callBack)
    }

    /**
     * 处理View恢复逻辑view
     */
    private class SaveHierarchyStateView(fragment: Fragment) :
        FrameLayout(fragment.requireContext()) {

        /**
         * 是否恢复View状态
         */
        var restoreHierarchyState = true

        override fun restoreHierarchyState(container: SparseArray<Parcelable>?) {
            if (restoreHierarchyState) {
                //view在进行状态恢复时是通过ID存取，如上次保存信息ID被重新分配到其他类型View上，使用上次ID信息就会报如下错误
                /*  protected void onRestoreInstanceState(Parcelable state) {
                    .....
                    if (state != null && !(state instanceof AbsSavedState)) {
                        throw new IllegalArgumentException("Wrong state class, expecting View State but "
                    + "received " + state.getClass().toString() + " instead. This usually happens "
                    + "when two views of different type have the same id in the same hierarchy. "
                    + "This view's id is " + ViewDebug.resolveId(mContext, getId()) + ". Make sure "
                    + "other views do not use the same id.");
                    .....
                    }
                */
                try {
                    super.restoreHierarchyState(container)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    if (NavUtil.debug) {
                        val msg = "fragment:${this.fragment} SaveHierarchyStateView:$this"
                        throw RuntimeException(msg, e)
                    }
                }

            }
        }

    }


}