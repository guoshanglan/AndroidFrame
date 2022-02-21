package androidx.fragment.app.result

import androidx.annotation.MainThread


/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/12 16:22
 *    desc   : Fragment消息启动器
 */
abstract class FragmentResultLauncher<I>{

    abstract fun launch(input: I)

    @MainThread
    abstract fun unregister()


    abstract fun getContract(): FragmentResultContract<I, *>


}