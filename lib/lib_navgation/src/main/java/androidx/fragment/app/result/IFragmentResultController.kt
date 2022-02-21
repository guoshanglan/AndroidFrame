package androidx.fragment.app.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/8/1 13:34
 *    desc   :
 */
interface IFragmentResultController : IFragmentForResult {

    fun fragmentResultBindFragment(fragment: Fragment, savedInstanceState: Bundle?)

    fun <I, O : Any> registerForFragmentResult(
        lifecycleOwner: LifecycleOwner,
        contract: FragmentResultContract<I, O>,
        callBack: FragmentResultCallback<O>
    ): FragmentResultLauncher<I>
}