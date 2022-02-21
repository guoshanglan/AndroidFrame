package androidx.fragment.app.result

import android.os.Bundle
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavUtil
import java.util.*

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/12 16:28
 *    desc   : Fragment 消息注册表
 */
abstract class FragmentResultRegistry {

    companion object {

        /**
         * 等待消费消息保存key
         */
        private const val KEY_COMPONENT_FRAGMENT_PENDING_RESULTS = "KEY_COMPONENT_FRAGMENT_PENDING_RESULT"

        /**
         * 已注册信息保存Key
         */
        private const val KEY_COMPONENT_FRAGMENT_REGISTERED_KEYS = "KEY_COMPONENT_FRAGMENT_REGISTERED_KEYS"

        fun isRestoreInstanceState(savedInstanceState: Bundle?): Boolean {
            val b = savedInstanceState ?: return false
            return b.containsKey(KEY_COMPONENT_FRAGMENT_PENDING_RESULTS) || b.containsKey(
                KEY_COMPONENT_FRAGMENT_REGISTERED_KEYS
            )
        }
    }

    /**
     * 已注册信息
     */
    private val mKeys: MutableSet<String> = mutableSetOf()

    /**
     * 回调
     */
    @Transient
    private val mKeyToCallback: HashMap<String, CallbackAndContract<*>> = HashMap()

    /**
     * 生命周期控制
     */
    private val mKeyToLifecycleContainers: MutableMap<String, LifecycleContainer> = mutableMapOf()

    /**
     * 已接收待消费消息
     */
    private val mPendingResults = Bundle()

    /**
     * 启动
     */
    @MainThread
    abstract fun <I, O : Any> onLaunch(requestKey: String, contract: FragmentResultContract<I, O>, input: I)

    /**
     * 设置fragment消息监听
     */
    @MainThread
    abstract fun onFragmentResultListener(requestKey: String)

    /**
     * 注册
     */
    fun <I, O : Any> register(key: String, lifecycleOwner: LifecycleOwner, contract: FragmentResultContract<I, O>, callback: FragmentResultCallback<O>): FragmentResultLauncher<I> {
        mKeys.add(key)
        val lifecycle = lifecycleOwner.lifecycle
        check(!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            ("LifecycleOwner " + lifecycleOwner + " is "
                    + "attempting to register while current state is "
                    + lifecycle.currentState + ". LifecycleOwners must call register before "
                    + "they are STARTED.")
        }
        var lifecycleContainer = mKeyToLifecycleContainers[key]
        if (lifecycleContainer == null) {
            lifecycleContainer = LifecycleContainer(lifecycle)
        }
        val observer = LifecycleEventObserver { _, event ->
            when {
                Lifecycle.Event.ON_START == event -> {
                    mKeyToCallback[key] = CallbackAndContract(callback, contract)
                    mPendingResults.getParcelable<Bundle>(key)?.let {
                        mPendingResults.remove(key)
                        callback.onFragmentResult(key, contract.parseResult(key, it))
                    }
                }
                Lifecycle.Event.ON_STOP == event -> {
                    mKeyToCallback.remove(key)
                }
                Lifecycle.Event.ON_DESTROY == event -> {
                    unregister(key)
                }
            }
        }
        lifecycleContainer.addObserver(observer)
        mKeyToLifecycleContainers[key] = lifecycleContainer

        return object : FragmentResultLauncher<I>() {

            override fun launch(input: I) {
                onFragmentResultListener(key)
                onLaunch(key, contract, input)
            }

            override fun unregister() {
                this@FragmentResultRegistry.unregister(key)
            }

            override fun getContract(): FragmentResultContract<I, *> {
                return contract
            }


        }
    }

    /**
     * 注册
     */
    fun <I, O : Any> register(key: String, contract: FragmentResultContract<I, O>, callback: FragmentResultCallback<O>): FragmentResultLauncher<I> {
        mKeys.add(key)
        mKeyToCallback[key] = CallbackAndContract(callback, contract)
        mPendingResults.getParcelable<Bundle>(key)?.let {
            mPendingResults.remove(key)
            callback.onFragmentResult(key, contract.parseResult(key, it))
        }
        return object : FragmentResultLauncher<I>() {

            override fun launch(input: I) {
                onFragmentResultListener(key)
                onLaunch(key, contract, input)
            }

            override fun unregister() {
                this@FragmentResultRegistry.unregister(key)
            }

            override fun getContract(): FragmentResultContract<I, *> {
                return contract
            }

        }
    }

    /**
     * 反注册
     */
    @MainThread
    fun unregister(key: String) {
        mKeys.remove(key)
        mKeyToCallback.remove(key)
        if (mPendingResults.containsKey(key)) {
            mPendingResults.remove(key)
        }
        val lifecycleContainer: LifecycleContainer? = mKeyToLifecycleContainers[key]
        if (lifecycleContainer != null) {
            lifecycleContainer.clearObservers()
            mKeyToLifecycleContainers.remove(key)
        }
    }

    /**
     * 被回收保存信息
     */
    fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray(KEY_COMPONENT_FRAGMENT_REGISTERED_KEYS, mKeys.toTypedArray())
        outState.putBundle(KEY_COMPONENT_FRAGMENT_PENDING_RESULTS, mPendingResults.clone() as Bundle)
    }

    /**
     * 恢复被回收保存信息
     */
    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        val b = savedInstanceState ?: return
        if (NavUtil.debug)Log.d("registerForFragment", "onRestoreInstanceState: $savedInstanceState")
        mPendingResults.putAll(b.getBundle(KEY_COMPONENT_FRAGMENT_PENDING_RESULTS))
        b.getStringArray(KEY_COMPONENT_FRAGMENT_REGISTERED_KEYS)?.toList()?.let {
            mKeys.addAll(it)
        }
        mKeys.forEach {
            onFragmentResultListener(it)
        }
    }

    /**
     * 处理结果
     */
    @MainThread
    fun dispatchResult(key: String, data: Bundle?): Boolean {
        if (NavUtil.debug)Log.d("registerForFragment", "dispatchResult: key:$key data:$data")
        val k = if (mKeys.contains(key)) key else return false
        if (NavUtil.debug)Log.d("registerForFragment", "dispatchResult: 2")
        val callbackAndContract = mKeyToCallback[k]?.let { it as CallbackAndContract<Any> }
        if (callbackAndContract?.mCallback == null) {
            mPendingResults.putParcelable(key, data)
        } else {
            val result = callbackAndContract.mContract.parseResult(k, data)
            callbackAndContract.mCallback.onFragmentResult(key, result)
        }
        return true
    }

    private class CallbackAndContract<O : Any> constructor(
        val mCallback: FragmentResultCallback<O>,
        val mContract: FragmentResultContract<*, O>
    )

    private class LifecycleContainer constructor(val mLifecycle: Lifecycle) {
        private val mObservers: ArrayList<LifecycleEventObserver> = ArrayList()
        fun addObserver(observer: LifecycleEventObserver) {
            mLifecycle.addObserver(observer)
            mObservers.add(observer)
        }

        fun clearObservers() {
            for (observer in mObservers) {
                mLifecycle.removeObserver(observer!!)
            }
            mObservers.clear()
        }

    }
}