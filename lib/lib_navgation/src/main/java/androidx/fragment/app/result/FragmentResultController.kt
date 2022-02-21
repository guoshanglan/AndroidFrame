package androidx.fragment.app.result

import android.app.Activity
import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.startFragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Dest
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicInteger

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/13 18:33
 *    desc   : fragment消息控制器
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class FragmentResultController():IFragmentResultController{

    companion object {

        /**
         * 返回值启动参数保存Key
         */
        const val KEY_FRAGMENT_FOR_RESULT_REQUEST = "androidx.navigation.fragment.Key_fragment_for_fesult_request"

        /**
         * 返回值启动参数前缀保存Key
         */
        private const val KEY_FRAGMENT_FRR_PREFIX = "androidx.navigation.fragment.fragment_frr_Prefix"

    }

    private lateinit var mFragment: WeakReference<Fragment>

    /**
     * 是否设置过返回信息
     */
    private var mSetResult = true

    /**
     * startFragmentForResult()生成的返回值启动请求参数
     */
    private var mFragmentForResultRequest: String? = null

    /**
     * registerForFragmentResult()自增RequestCode
     */
    private val mNextLocalRequestCode = AtomicInteger()

    /**
     * 返回值启动RequestKey前缀
     */
    private lateinit var fragmentRqPrefix: String

    /**
     * 默认返回值启动fragment启动器
     */
    private var mFragmentForResultLauncher: FragmentResultLauncher<Dest>? = null

    /**
     * 返回值启动注册表
     */
    private val mFragmentResultRegistry = lazy {
        object : FragmentResultRegistry() {

            override fun onFragmentResultListener(requestKey: String) {
                val fragment = mFragment.get() ?: return
                val activity = fragment.requireActivity()
                activity.supportFragmentManager.setFragmentResultListener(requestKey, fragment) { requestKey, data ->
                    dispatchResult(requestKey, data)
                }
            }

            override fun <I, O : Any> onLaunch(requestKey: String, contract: FragmentResultContract<I, O>, input: I) {
                val fragment = mFragment.get() ?: return
                val activity = fragment.requireActivity()
                val dest = contract.createDest(activity, input).let { dest ->
                    dest.arguments?.let {
                        it.putString(KEY_FRAGMENT_FOR_RESULT_REQUEST, requestKey)
                        dest
                    } ?: kotlin.run {
                        Dest.Builder(dest).setArguments(bundleOf(KEY_FRAGMENT_FOR_RESULT_REQUEST to requestKey)).build()
                    }
                }
                fragment.startFragment(dest)
            }
        }
    }

    override fun <I, O : Any> registerForFragmentResult(lifecycleOwner: LifecycleOwner, contract: FragmentResultContract<I, O>, callBack: FragmentResultCallback<O>): FragmentResultLauncher<I> {
        val reqKey = "${fragmentRqPrefix}i:${mNextLocalRequestCode.getAndDecrement()}"
        return mFragmentResultRegistry.value.register(reqKey, lifecycleOwner, contract, callBack)
    }

    /**
     * 初始化返回值启动器
     */
    private fun initFragmentForResultLauncher(forResultRequest: String) {
        mFragmentForResultLauncher?.unregister()
        mFragmentForResultLauncher = mFragmentResultRegistry.value.register(
            forResultRequest,
            FragmentResultContracts.StartFragmentForResult(),
            object : FragmentResultCallback<FragmentResult> {

                override fun onFragmentResult(requestKey: String, result: FragmentResult) {
                    mFragmentForResultLauncher?.unregister()
                    mFragmentForResultLauncher = null
                    val prefix = getFragmentForResultRrefix()
                    val requestCode = requestKey.substring(prefix.length, requestKey.length).toInt()
                    mFragment.get()?.let { it as IFragmentForResult }
                        ?.onFragmentForResult(requestCode, result.resultCode, result.data)
                }

            })
        mFragmentForResultRequest = forResultRequest
    }

    /**
     * 获取返回值启动前缀
     */
    private fun getFragmentForResultRrefix(): String {
        return "${fragmentRqPrefix}rc:"
    }

    override fun fragmentResultBindFragment(fragment: Fragment, savedInstanceState: Bundle?) {
        if (fragment !is IFragmentForResult) {
            throw RuntimeException("${fragment.javaClass.name} !is IFragmentResult")
        }
        mFragment = WeakReference(fragment)
        savedInstanceState?.getString(KEY_FRAGMENT_FRR_PREFIX, null)?.let {
            fragmentRqPrefix = it
        } ?: kotlin.run {
            fragmentRqPrefix = "fragment_rq[${Integer.toHexString(System.identityHashCode(this))}]#"
        }
        if (FragmentResultRegistry.isRestoreInstanceState(savedInstanceState)) {
            mFragmentResultRegistry.value.onRestoreInstanceState(savedInstanceState)
        }
        savedInstanceState?.getString(KEY_FRAGMENT_FOR_RESULT_REQUEST, null)?.let {
            initFragmentForResultLauncher(it)
        }
        fragment.parentFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentSaveInstanceState(
                    fm: FragmentManager,
                    f: Fragment,
                    outState: Bundle
                ) {
                    super.onFragmentSaveInstanceState(fm, f, outState)
                    if (f == mFragment?.get()){
                        outState.putString(KEY_FRAGMENT_FRR_PREFIX, fragmentRqPrefix)
                        mFragmentForResultRequest?.let {
                            outState.putString(KEY_FRAGMENT_FOR_RESULT_REQUEST, it)
                        }
                        if (mFragmentResultRegistry.isInitialized()) {
                            mFragmentResultRegistry.value.onSaveInstanceState(outState)
                        }
                    }
                }

                override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentDestroyed(fm, f)
                    if (f == mFragment?.get()){
                        mFragmentForResultLauncher?.unregister()
                        fm.unregisterFragmentLifecycleCallbacks(this)
                    }
                }

            }, false
        )
        //接收返回值启动请求参数
        fragment.arguments?.getString(KEY_FRAGMENT_FOR_RESULT_REQUEST)?.let { forFragmentRequest ->
            mSetResult = false
            fragment.requireActivity().supportFragmentManager.registerFragmentLifecycleCallbacks(
                object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                        super.onFragmentStarted(fm, f)
                        if (!mSetResult && f is IFragmentForResult && forFragmentRequest == f.getFragmentForResultRequest()) {
                            mFragment.get()?.let { it as IFragmentForResult }
                                ?.setResult(Activity.RESULT_CANCELED, null)
                        }
                    }

                    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                        super.onFragmentDestroyed(fm, f)
                        if (f == mFragment.get()) {
                            fragment.requireActivity().supportFragmentManager.unregisterFragmentLifecycleCallbacks(
                                this
                            )
                        }
                    }

                },
                true
            )
        }
    }

    override fun getFragmentForResultRequest():String?{
        return mFragmentForResultRequest
    }

    override fun startFragmentForResult(requestCode: Int, dest: Dest) {
        val forResultRequest = "${getFragmentForResultRrefix()}${requestCode}"
        initFragmentForResultLauncher(forResultRequest)
        mFragmentForResultLauncher?.launch(dest)
    }

    override fun setResult(resultCode: Int, data: Bundle?) {
        mSetResult = true
        mFragment.get()?.arguments?.getString(KEY_FRAGMENT_FOR_RESULT_REQUEST)?.let {
            val activity = mFragment.get()?.requireActivity() ?: return
            activity.supportFragmentManager.setFragmentResult(it, bundleOf("data" to FragmentResult(it, resultCode, data)))

        }
    }

    /**
     * 接收返回值启动结果
     */
    override fun onFragmentForResult(requestCode: Int, resultCode: Int, data: Bundle?) {
//        mFragment.get()?.let {
//            if (it is IFragmentForResult){
//                it.onFragmentForResult(requestCode,resultCode,data)
//            }
//        }
    }


}