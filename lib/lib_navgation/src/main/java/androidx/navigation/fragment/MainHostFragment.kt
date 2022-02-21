package androidx.navigation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.RestrictTo
import androidx.core.os.bundleOf
import androidx.navigation.*

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/6/29 10:03
 *    desc   : 主流程切换控制NavHost Fragment
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class MainHostFragment : NavHostFragment() {

    companion object {
        fun createArguments(startDestination: String, startDestinationArgs: Bundle?): Bundle {
            return bundleOf(
                "startDestination" to startDestination,
                "startDestinationArgs" to startDestinationArgs
            )
        }
    }

    private lateinit var mDDRestorer: DynamicDestiantionRestorer
    /**
     * 是否开启过滤LeakCanary获取View
     */
    private var mOpenFilterLeakCanary = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        DynamicDestiantionRestorer.saveInstanceState(navController, outState, mDDRestorer.mGraphId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mDDRestorer = DynamicDestiantionRestorer(savedInstanceState)
        super.onCreate(savedInstanceState)
        val startDestinationArgs = arguments?.getBundle("startDestinationArgs")
        if (mDDRestorer.restoreGraph(navController, startDestinationArgs)) {
            //成功恢复xml方式加载的导航图
            return
        }
        if (navController.navGraph() != null) {
            //其他方式设置过导航图(xml方式，或在子类动态加载)
            return
        }
        arguments?.getString("startDestination", null)?.let { startCalssName ->
            //动态设置导航图
            val graphId = NavUtil.createDestinationId(this.javaClass.name)
            val startDestId = NavUtil.createDestinationId(startCalssName)
            navController.createGraph(graphId, startDestId) {}.let {
                it.requireDestinationId(navController, startCalssName)
                mDDRestorer.setGraph(navController, it, startDestinationArgs)
            }
        } ?: kotlin.run {
            throw IllegalArgumentException("not startDestination,getStartDestination() not null")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mOpenFilterLeakCanary = false
        return super.onCreateView(inflater, container, savedInstanceState)
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
}