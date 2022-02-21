package base2app.dialog

import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.lib_base.R
import base2app.dialog.DialogHelper.bindFragmentByDialog
import base2app.ex.isScreenPortrait
import base2app.util.ToastUtil
import java.lang.ref.WeakReference

/**
 * dialog创建
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
abstract class BaseDialog : DialogInterface.OnShowListener, DialogInterface.OnCancelListener,
    DialogInterface.OnDismissListener, IDialog,
    DialogHelper.FragmentLifecycleCallBack {

    protected var TAG: String? = null
    private var fragment: WeakReference<Fragment>? = null
    protected var context: WeakReference<Context>? = null
    protected var dialog: Dialog? = null
    private var lifeCycles: ArrayList<DialogLifeCycle>? = arrayListOf()
    protected var width = WindowManager.LayoutParams.WRAP_CONTENT
    protected var height = WindowManager.LayoutParams.WRAP_CONTENT
    private var allowRecover = false
    private var dialogContentView: View? = null

    val containerView: View?
        get() = dialogContentView

    protected val dialogStyle: Int
        get() = R.style.DialogTransparent30

    protected abstract val layout: Int

    val isShowing: Boolean
        get() = dialog?.isShowing ?: false

    fun requireView(): View {
        return dialogContentView!!
    }

    protected constructor(context: Context) {
        initView(context, 0)
    }

    protected constructor(context: Context, @StyleRes theme: Int = 0) {
        initView(context, theme)
    }

    protected constructor(context: Context, w: Int, h: Int) {
        this.width = w
        this.height = h
        initView(context, 0)
    }

    protected constructor(context: Context, w: Int, h: Int, @StyleRes theme: Int) {
        this.width = w
        this.height = h
        initView(context, theme)
    }

    protected constructor(fragment: Fragment) {
        initView(fragment.requireContext(), 0)
        bindFragmentByDialog(this, fragment)
    }

    protected constructor(fragment: Fragment, @StyleRes theme: Int = 0) {
        initView(fragment.requireContext(), theme)
        bindFragmentByDialog(this, fragment)
    }

    protected constructor(fragment: Fragment, w: Int, h: Int) {
        this.width = w
        this.height = h
        initView(fragment.requireContext(), 0)
        bindFragmentByDialog(this, fragment)
    }

    protected constructor(fragment: Fragment, w: Int, h: Int, @StyleRes theme: Int) {
        this.width = w
        this.height = h
        initView(fragment.requireContext(), theme)
        bindFragmentByDialog(this, fragment)
    }

    override fun getBindFragment(): Fragment? {
        return fragment?.get()
    }

    override fun setCanRecover(allowRecover: Boolean): BaseDialog {
        this.allowRecover = allowRecover
        return this
    }

    override fun showDialog() {
        show()
    }

    override fun isCanRecover(): Boolean {
        return allowRecover
    }

    private fun initView(context: Context, @StyleRes theme: Int) {
        this.TAG = this.javaClass.name
        this.context = WeakReference(context)
        dialog = Dialog(context, if (theme != 0) theme else dialogStyle).also {
            it.setOnCancelListener(this)
            it.setOnDismissListener(this)
        }
        val layout = layout
        val customDialog = LayoutInflater.from(context).inflate(layout, null)
        this.dialogContentView = customDialog
        val params = WindowManager.LayoutParams()
        params.width = width
        params.height = height
        dialog?.setContentView(customDialog, params)
        dialog?.setOnShowListener(this)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        lifeCycles?.forEach {
            it.onDialogDismiss(this.javaClass.name)
        }
    }

    override fun onCancel(dialog: DialogInterface?) {
    }

    fun getDialogContentView(): View? {
        return dialogContentView
    }

    override fun setLifeCycle(lifeCycle: DialogLifeCycle): BaseDialog {
        this.lifeCycles?.add(lifeCycle)
        return this
    }

    open fun show() {
        if (context?.get() != null && dialog?.isShowing == false) {
            dialog?.show()
        }

        if (!isScreenPortrait()) {
            dialog?.window?.let {
                fullScreenImmersive(it.decorView)
                it.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                )
                it.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            }
        }
    }

    private fun fullScreenImmersive(view: View) {
        ViewCompat.getWindowInsetsController(view)?.hide(WindowInsetsCompat.Type.systemBars())
    }

    open fun dismiss() {
        try {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onShow(dialog: DialogInterface) {
        lifeCycles?.forEach {
            it.onDialogShow(this.javaClass.name)
        }
    }

    /**
     * 是否允许外部点击来取消对话框
     */
    protected fun changeDialogOutside(isCanClick: Boolean) {
        dialog?.setCanceledOnTouchOutside(isCanClick)
    }

    /**
     * 是否要屏蔽返回键来取消对话框
     */
    protected fun ignoreBackPressed() {
        dialog?.setCancelable(false)
        /*设置onKeyListener*/
        dialog?.setOnKeyListener { dialog, keyCode, event -> keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0 }
    }

    override fun setBindFragment(fragment: WeakReference<Fragment>) {
        this.fragment = fragment
    }

    override fun onFragmentResume() {
        lifeCycles?.forEach {
            it.onFragmentResume()
        }
    }

    override fun onFragmentDestroy() {
        lifeCycles?.forEach {
            it.onDialogDismiss(this.javaClass.name)
        }
        this.lifeCycles?.clear()
        this.lifeCycles = null
        this.dialogContentView = null
        this.fragment?.clear()
        this.fragment = null
        this.context?.clear()
        this.context = null
        dialog?.let {
            it.setOnCancelListener(null)
            it.setOnDismissListener(null)
        }
        this.dialog?.dismiss()
        this.dialog = null
    }

    protected fun setWindowAnimations(@StyleRes animation: Int) {
        val window = dialog?.window ?: return
        // //设置窗口弹出动画
        window.setWindowAnimations(animation)
    }

    protected fun updatePosition(x: Int, y: Int, gravity: Int) {
        val window = dialog?.window ?: return
        val lp = window.attributes
        lp.gravity = gravity
        lp.x = x
        lp.y = y
        window.attributes = lp
    }

    @TargetApi(Build.VERSION_CODES.P)
    protected fun usePDisplayCutout(cutoutMode: Int) {
        val window = dialog!!.window ?: return
        val lp = window.attributes
        lp.layoutInDisplayCutoutMode = cutoutMode
        window.attributes = lp
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