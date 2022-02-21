package com.zhuorui.commonwidget

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import base2app.BaseApplication
import base2app.ex.mainThread
import com.zhuorui.commonwidget.dialog.ProgressDialog
import com.zhuorui.commonwidget.model.IZRLoading

/**
 * @date 2021/11/17 10:24
 * @desc 显示ProgressDialog的包装类，用于快速显示和隐藏loading
 */
class ZRLoading : LifecycleEventObserver, IZRLoading {

    private var progressDialog: ProgressDialog? = null

    override fun showLoading(msg: String?) {
        if (progressDialog?.isShowing == true) return
        mainThread {
            if (progressDialog == null) {
                BaseApplication.baseApplication.topActivity?.topFragment?.let {
                    progressDialog = ProgressDialog.create(it)
                    it.lifecycle.addObserver(this)
                }
            }
            if (!msg.isNullOrEmpty()) progressDialog?.setMessage(msg)
            progressDialog?.show()
        }
    }

    override fun closeLoading() {
        progressDialog?.takeIf { it.isShowing }?.let {
            mainThread { it.dismiss() }
        }
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) closeLoading()
    }
}