package androidx.fragment.app.result

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.navigation.Dest

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/12 16:14
 *    desc   : 公共协议
 */
class FragmentResultContracts {

    class StartFragmentForResult : FragmentResultContract<Dest, FragmentResult>() {

        override fun createDest(context: Context, input: Dest): Dest {
            return input
        }

        override fun parseResult(requestKey: String, bundle: Bundle?): FragmentResult {
            return bundle?.getParcelable("data")
                    ?: FragmentResult(requestKey, Activity.RESULT_CANCELED, null)
        }
    }

}