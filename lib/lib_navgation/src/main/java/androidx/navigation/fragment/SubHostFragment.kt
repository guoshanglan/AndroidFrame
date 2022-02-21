package androidx.navigation.fragment

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.result.*
import androidx.navigation.*

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/6/29 14:37
 *    desc   : 子流程切换控制NavHost Fragment
 *    contentLayoutId:内容视图lId,
 *    containerId:fragment容器Id
 *    navigationId:导航图Id
 *
 */
abstract class SubHostFragment @JvmOverloads constructor(
    @LayoutRes protected val contentLayoutId: Int? = 0,
    @IdRes protected val containerId: Int? = 0,
    @NavigationRes protected val navigationId: Int? = 0
) : NavHostFragment(), IFragmentResultController by FragmentResultController(), AnimGenerator.AnimationListener {

    private lateinit var mDDRestorer: DynamicDestiantionRestorer

    /**
     * 是否开启过滤LeakCanary获取View
     */
    private var mOpenFilterLeakCanary = false

    /**
     * 动画生成器
     */
    private val mAnimGenerator = AnimGenerator()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        DynamicDestiantionRestorer.saveInstanceState(navController, outState, navigationId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //DynamicDestiantionRestorer 创建必须在父类onCreate前
        mDDRestorer = DynamicDestiantionRestorer(savedInstanceState)
        super.onCreate(savedInstanceState)
        val startDestinationArgs = getStartDestinationArgs(savedInstanceState)
        val navId = navigationId?:0
        if (navId != 0) {
            if (!mDDRestorer.restoreGraph(navController, startDestinationArgs)) {
                navController.setGraph(navId, startDestinationArgs)
            }
        } else {
            getStartDestination()?.let { startCalssName ->
                val graphId = NavUtil.createDestinationId(this.javaClass.name)
                val startDestId = NavUtil.createDestinationId(startCalssName)
                navController.createGraph(graphId, startDestId) {}.let {
                    it.requireDestinationId(navController, startCalssName)
                    mDDRestorer.setGraph(navController, it, startDestinationArgs)
                }
            } ?: kotlin.run {
                throw IllegalArgumentException("not startDestination,constructor navigationId != 0 or getStartDestination() not null")
            }
        }
        mAnimGenerator.bindFragment(this)
        fragmentResultBindFragment(this,savedInstanceState)
//        NavLog.logBackStack(this)
    }

    /**
     * 获取开始目的地
     * @return calssName
     */
    protected open fun getStartDestination():String?{
        return null
    }

    /**
     * 获取开始目的地的参数
     */
    protected open fun getStartDestinationArgs(savedInstanceState: Bundle?): Bundle?{
        return null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mOpenFilterLeakCanary = false
        val layoutId = contentLayoutId?:0
        return if (layoutId != 0) {
            inflater.inflate(layoutId, container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    @CallSuper
    override fun onDestroyView() {
        mOpenFilterLeakCanary = true
        super.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        mOpenFilterLeakCanary = false
        super.onDestroy()
    }

    override fun onCreateNavController(navController: NavController) {
        super.onCreateNavController(navController)
        //绑定Container View
        navController.navigatorProvider.addNavigator(
            FragmentNavigator(
                requireContext(),
                childFragmentManager,
                containerId?:0
            )
        )
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return mAnimGenerator.createAnimation(transit, enter, nextAnim)
            ?: super.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        return mAnimGenerator.createAnimator(transit, enter, nextAnim)
            ?: super.onCreateAnimator(transit, enter, nextAnim)
    }

    final override fun onEnterAnimStart() {
        childFragmentManager.fragments.forEach {
            if (it is DestinationFragment && it.isResumed) {
                it.dispatchEnterAniming()
            }
        }
    }

    final override fun onEnterAnimEnd() {
        childFragmentManager.fragments.forEach {
            if (it is DestinationFragment && it.isResumed) {
                it.dispatchEnterAnimEnd()
            }
        }
    }

    override fun isEnterAnim(): Boolean {
        return mAnimGenerator.isRunging()
    }

    protected fun getContainerId(): Int {
        return containerId?:0
    }

    override fun getView(): View? {
        //通过view返回null,禁用LeakCanary在onDestoryView检测泄漏功能，在onDestoryView会存在误报
        return if (isFormLeakCanary()) null else super.getView()
    }

    /**
     * 是否LeakCanary调用
     */
    private fun isFormLeakCanary(): Boolean {
        if (NavUtil.debug && mOpenFilterLeakCanary) {
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

    /**
     * 注册Fragment消息监听
     */
    fun <I, O : Any> registerForFragmentResult(
        contract: FragmentResultContract<I, O>,
        callBack: FragmentResultCallback<O>
    ): FragmentResultLauncher<I> {
        return registerForFragmentResult(this, contract, callBack)
    }

}