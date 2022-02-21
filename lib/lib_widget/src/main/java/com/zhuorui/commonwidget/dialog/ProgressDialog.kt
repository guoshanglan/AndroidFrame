package com.zhuorui.commonwidget.dialog

import android.content.DialogInterface
import android.os.Looper
import androidx.fragment.app.Fragment
import base2app.dialog.BaseDialog
import base2app.ex.text
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.ZRLoadingView

/**
 * Date: 2019/8/22
 * Desc:进度环框
 * */

class ProgressDialog : BaseDialog {

    constructor(fragment: Fragment) : super(fragment, R.style.DialogTransparent) {
        changeDialogOutside(false)
        ignoreBackPressed()
    }

    constructor(fragment: Fragment, canceledOnTouchOutside: Boolean, ignoreBack: Boolean) : super(fragment, R.style.DialogTransparent) {
        changeDialogOutside(canceledOnTouchOutside)
        if (ignoreBack)
            ignoreBackPressed()
    }

    private  var loadingView: ZRLoadingView? = null

    override val layout: Int
        get() = R.layout.layout_dialog_loading

    init{
        loadingView = getDialogContentView()?.findViewById(R.id.logind)
        setMessage(text(R.string.load_state_default))
    }

    fun setMessage(msg: String?) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            loadingView?.setMessage(msg)
        } else {
            loadingView?.post {
                loadingView?.setMessage(msg)
            }
        }
    }

    override fun onShow(dialog: DialogInterface) {
        super.onShow(dialog)
        // 启动动画
        loadingView?.start()
    }

    override fun onFragmentDestroy() {
        super.onFragmentDestroy()
        // 关闭动画
        loadingView?.stop()
        loadingView = null
    }


    companion object {
        fun create(fragment: Fragment, canceledOnTouchOutside: Boolean = false, ignoreBack: Boolean = false): ProgressDialog {
            return ProgressDialog(fragment, canceledOnTouchOutside, ignoreBack)
        }
    }
}