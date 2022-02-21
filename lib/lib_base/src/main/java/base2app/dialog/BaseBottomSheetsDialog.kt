package base2app.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.example.lib_base.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import base2app.dialog.DialogHelper.bindFragmentByBottom
import base2app.ex.color
import base2app.util.ToastUtil
import kotlinx.android.extensions.LayoutContainer
import java.lang.ref.WeakReference

/**
 * 定义基础的BottomSheets弹窗
 */
abstract class BaseBottomSheetsDialog protected constructor(fragment: Fragment) :
    DialogInterface.OnShowListener,
    DialogInterface.OnDismissListener, DialogInterface.OnCancelListener, LayoutContainer, IDialog,
    DialogHelper.FragmentLifecycleCallBack {
    protected var TAG: String? = null
    protected var context: WeakReference<Context>? = null
    protected var fragment: WeakReference<Fragment>? = null
    private var lifeCycles: ArrayList<DialogLifeCycle>? = null
    protected var dialog: BottomSheetDialog? = null
    private var allowRecover: Boolean = false

    protected open fun getWidth(): Int{
        return FrameLayout.LayoutParams.WRAP_CONTENT
    }

    protected open fun getHeight(): Int{
        return FrameLayout.LayoutParams.WRAP_CONTENT
    }

    protected open val dialogStyle: Int
        get() = R.style.BottomSheetDialogStyle

    protected abstract val layout: Int

    fun requireView(): View {
        return dialog?.window?.decorView!!
    }

    override val containerView: View?
        get() = dialog?.window?.decorView

    init {
        init(fragment.requireContext())
        bindFragmentByBottom(this, fragment)
    }

    private fun init(context: Context?) {
        this.TAG = this.javaClass.name
        this.context = WeakReference(context)
        initView()
    }

    override fun onFragmentResume() {

    }

    override fun getBindFragment(): Fragment? {
        return fragment?.get()
    }

    override fun setCanRecover(allowRecover: Boolean): BaseBottomSheetsDialog {
        this.allowRecover = allowRecover
        return this
    }

    protected open fun autoWH(): Boolean {
        return true
    }

    private fun initView() {
        val ctx = context?.get() ?: return
        dialog = BottomSheetDialog(ctx, dialogStyle)
        val layout = layout
        if (autoWH()) {
            dialog?.setContentView(layout)
        } else {
            val view = View.inflate(ctx, layout, null)
            val lp = FrameLayout.LayoutParams(getWidth(), getHeight())
            dialog?.setContentView(view, lp)
        }
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.setOnShowListener(this)
        dialog?.setOnDismissListener(this)
        dialog?.setOnCancelListener(this)
        /*设置dialog的视图默认背景色为transparent透明色*/
        try {
            val parentView =
                dialog?.delegate?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            color(android.R.color.transparent).let { parentView?.setBackgroundColor(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun setCancelable(clickOutSideCancel: Boolean) {
        dialog?.setCancelable(clickOutSideCancel)
    }

    open fun dismiss() {
        lifeCycles?.forEach {
            it.onDialogDismiss(this.javaClass.name)
        }
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

    protected fun ignoreBackPressed() {
        /*设置onKeyListener*/
        dialog?.setOnKeyListener { _, keyCode, event -> keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0 }
    }

    final override fun onShow(dialog: DialogInterface) {
        lifeCycles?.forEach {
            it.onDialogShow(this.javaClass.name)
        }
        dialogOnShow()
    }

    override fun setBindFragment(fragment: WeakReference<Fragment>) {
        this.fragment = fragment
    }

    @CallSuper
    override fun onFragmentDestroy() {
        lifeCycles?.forEach {
            it.onDialogDismiss(this.javaClass.name)
        }
        if (dialog?.isShowing == true) {
            dismiss()
        }
        this.lifeCycles?.clear()
        this.lifeCycles = null
        this.fragment?.clear()
        this.fragment = null
        this.context = null
        this.dialog = null
    }

    override fun setLifeCycle(lifeCycle: DialogLifeCycle): BaseBottomSheetsDialog {
        lifeCycles?.add(lifeCycle)
        return this
    }

    override fun showDialog() {
        show()
    }

    override fun isCanRecover(): Boolean {
        return allowRecover
    }

    @CallSuper
    override fun onDismiss(dialog: DialogInterface) {
        lifeCycles?.forEach {
            it.onDialogDismiss(this.javaClass.name)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
    }

    protected open fun dialogOnShow() {}

    fun show() {
        if (dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    protected fun toast(@StringRes strResId: Int) {
        ToastUtil.instance.toast(strResId)
    }

    protected fun toast(@StringRes strRes: Int, shortDuration: Boolean) {
        if (shortDuration) {
            ToastUtil.instance.toast(strRes)
        } else {
            ToastUtil.instance.toastLong(strRes)
        }
    }

    protected fun toast(str: String, shortDuration: Boolean) {
        if (shortDuration) {
            ToastUtil.instance.toast(str)
        } else {
            ToastUtil.instance.toastLong(str)
        }
    }

}
