package base2app.util

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import base2app.BaseApplication
import base2app.ex.logd
import base2app.ex.mainThread
import base2app.ex.text
import com.example.lib_base.R
import java.lang.ref.WeakReference
import java.lang.reflect.Field

/**
 * Created by xieyingwu on 2018/5/8
 * Toast工具类
 */
@SuppressLint("DiscouragedPrivateApi")
class ToastUtil private constructor() {

    private var centerToast: WeakReference<Toast>? = null
    private var lastShowTime = 0L   //最后一次弹出的时间
    private var longShowTime = 3500     //Toast设置LENGTH_SHORT属性显示的时间
    private var shortShowTime = 2000    //Toast设置LENGTH_LONG属性显示的时间

    private object Builder {
        val instance = ToastUtil()
    }

    fun toast(@StringRes strRes: Int) {
        toastCenter(strRes, Toast.LENGTH_SHORT)
    }

    fun toast(str: CharSequence) {
        if (TextUtils.isEmpty(str)) return
        toastCenter(str, Toast.LENGTH_SHORT)
    }

    fun toastLong(@StringRes res: Int) {
        toastCenter(res, Toast.LENGTH_LONG)
    }

    fun toastLong(cs: CharSequence) {
        if (TextUtils.isEmpty(cs)) return
        toastCenter(cs, Toast.LENGTH_LONG)
    }

    /**
     * 自定义居中的toast
     */
    private fun toastCenter(@StringRes res: Int, duration: Int) {
        toastCenter(text(res), duration)
    }

    /**
     * 自定义居中的toast
     */
    private fun toastCenter(cs: CharSequence, duration: Int) {
        mainThread {
            realToastCenter(cs.toString(), duration)
        }
    }

    private fun realToastCenter(cs: String, duration: Int) {
        if (filterToast(cs, duration)) return
        createToast().let {
            it.view?.findViewById<TextView>(R.id.toast_tv)?.text = cs
            it.setGravity(Gravity.CENTER, 0, 0)
            it.duration = duration
            it.show()
        }
    }

    /**
     * 过滤相同内容，重复的toast
     */
    private fun filterToast(cs: String, duration: Int): Boolean {
        val v = centerToast?.get()?.view
        if (v is TextView) {
            if (v.text == cs) {
                if (duration == Toast.LENGTH_SHORT) {
                    if (System.currentTimeMillis() - lastShowTime < shortShowTime) return true
                } else {
                    if (System.currentTimeMillis() - lastShowTime < longShowTime) return true
                }
            }
        }
        return false
    }

    @SuppressLint("InflateParams")
    private fun createToast(): Toast {
        lastShowTime = System.currentTimeMillis()
        return Toast(BaseApplication.context).apply {
            view = LayoutInflater.from(BaseApplication.context).inflate(R.layout.layout_toast, null)
        }.also {
            hookToast(it)
            centerToast = WeakReference(it)
        }
    }

    fun toastCenter(@StringRes res: Int) {
        toastCenter(res, Toast.LENGTH_SHORT)
    }

    fun toastCenter(cs: CharSequence) {
        if (TextUtils.isEmpty(cs)) return
        toastCenter(cs, Toast.LENGTH_SHORT)
    }

    fun toastCenterLong(@StringRes res: Int) {
        toastCenter(res, Toast.LENGTH_LONG)
    }

    fun toastCenterLong(cs: CharSequence) {
        if (TextUtils.isEmpty(cs)) return
        toastCenter(cs, Toast.LENGTH_LONG)
    }

    fun cancel() {
        centerToast?.get()?.cancel()
    }


    companion object {

        val instance: ToastUtil
            get() = Builder.instance
    }


    private var sFieldTN: Field? = null

    private var sFieldTNHandler: Field? = null

    init {
        if (isAndroid7()) {
            try {
                sFieldTN = Toast::class.java.getDeclaredField("mTN")
                sFieldTN?.isAccessible = true
                sFieldTNHandler = sFieldTN?.type?.getDeclaredField("mHandler")
                sFieldTNHandler?.isAccessible = true
            } catch (e: Exception) {
                logd("ToastUtils class reflex error:${e.message}")
            }
        }
    }

    private fun hookToast(toast: Toast) {
        if (isAndroid7()) {
            try {
                sFieldTN?.get(toast)?.let { tn ->
                    val preHandler = sFieldTNHandler?.get(tn)
                    if (preHandler is Handler) {
                        sFieldTNHandler?.set(tn, SafeToastHandler(preHandler))
                    }
                }
            } catch (e: Exception) {
                logd("ToastUtils class hook toast error:${e.message}")
            }
        }
    }

    /**
     * 是否为Android 7.0相关版本
     */
    private fun isAndroid7(): Boolean {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1 && Build.VERSION.SDK_INT > Build.VERSION_CODES.M
    }


    class SafeToastHandler(private val handler: Handler) : Handler() {

        /**
         * 处理Android 7相关版本toast 偶现的闪退问题
         * 相关内容查看 https://blog.csdn.net/wuchuy/article/details/105035529
         */
        override fun dispatchMessage(msg: Message) {
            try {
                super.dispatchMessage(msg)
            } catch (e: Exception) {
                logd("ToastUtils class show toast token valid")
            }
        }

        override fun handleMessage(msg: Message) {
            handler.handleMessage(msg)
        }
    }
}
