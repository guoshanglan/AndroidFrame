package androidx.navigation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/7 15:07
 *    desc   :
 */
interface SingleDestiantion:INewArguments {

    companion object {

        fun getRequestKey(fragment: Fragment): String {
            return getRequestKey(fragment.javaClass.name)
        }

        fun getRequestKey(className: String): String {
            return "${className}:onNewAuments"
        }

        fun setFragmentResult(context: Context,className: String,value: Bundle) {
            (context as FragmentActivity).supportFragmentManager.setFragmentResult(
                getRequestKey(className),
                value
            )
        }
    }


}