package androidx.fragment.app.result

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/12 15:53
 *    desc   : fragment消息回调
 */
interface FragmentResultCallback<O> {

    fun onFragmentResult(requestKey:String, result: O)

}