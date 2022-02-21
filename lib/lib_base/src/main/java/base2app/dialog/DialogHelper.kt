package base2app.dialog

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import base2app.BaseApplication
import java.lang.ref.WeakReference

/**
 * @date 2021/8/5 17:09
 * @desc Dialog与Fragment绑定生命周期帮助类
 */
object DialogHelper {

    /**
     * 底部弹出dialog与fragment绑定生命周期
     */
    fun bindFragmentByBottom(dialog: BaseBottomSheetsDialog, fragment: Fragment) {
        bindFragment(dialog, fragment)
    }

    /**
     * 普通dialog与fragment绑定生命周期
     */
    fun bindFragmentByDialog(dialog: BaseDialog, fragment: Fragment) {
        bindFragment(dialog, fragment)
    }

    /**
     * 与fragment绑定上关系，fragment退出时，dialog一起关闭
     */
    private fun bindFragment(dialog: Any, fragment: Fragment) {
        val callBack = dialog as FragmentLifecycleCallBack
        callBack.setBindFragment(WeakReference(fragment))
        fragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    callBack.onFragmentResume()
                } else if (event == Lifecycle.Event.ON_PAUSE) {
                    if (!BaseApplication.baseApplication.isInBackground && fragment.activity?.lifecycle?.currentState != Lifecycle.State.STARTED
                    ) {
                        when (dialog) {
                            is BaseBottomSheetsDialog -> {
                                dialog.dismiss()
                            }
                            is BaseDialog -> {
                                dialog.dismiss()
                            }
                        }
                    }
                } else if (event == Lifecycle.Event.ON_DESTROY) {
                    callBack.onFragmentDestroy()
                }
            }
        })
    }

    interface FragmentLifecycleCallBack {

        /**
         * 设置当前绑定的fragment
         */
        fun setBindFragment(fragment: WeakReference<Fragment>)

        /**
         * fragment执行onDestroy生命周期回调
         */
        fun onFragmentDestroy()

        /**
         * fragment执行onResume生命周期回调
         */
        fun onFragmentResume()

    }

}