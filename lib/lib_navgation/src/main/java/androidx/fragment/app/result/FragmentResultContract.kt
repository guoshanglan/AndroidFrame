package androidx.fragment.app.result

import android.content.Context
import android.os.Bundle
import androidx.navigation.Dest

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/12 15:41
 *    desc   : fragment消息接收协议
 */
abstract class FragmentResultContract<I, O> {

    abstract fun createDest(context: Context, input: I): Dest

    abstract fun parseResult(requestKey: String, bundle: Bundle?): O

}