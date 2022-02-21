package androidx.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.MainHostFragment
import java.lang.ref.SoftReference

/**
 * author : liuwei
 * e-mail : vsanliu@foxmail.com
 * date   : 2021/7/6 10:18
 * desc   :
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal object NavUtil {

    var debug = false

    var showSoftInputFragment:Int = 0
    var showSoftInputView:Int = 0
    var hideSoftInputFragment:Int = 0

    fun openDebug() {
        debug = true
    }

    /**
     * 生成className 的目的地Id
     */
    fun createDestinationId(className: String): Int {
//        return System.identityHashCode(className)
        return DestIdSP.instance.requireDestinationId(className)
    }

    /**
     * 获取主流程NavController
     */
    fun findMainNavController(fragment: Fragment): NavController {
        fragment.parentFragment?.let {
            if (it is MainHostFragment) {
                return it.navController
            }
        }
        return findMainNavController(fragment.requireActivity())
    }

    /**
     * 获取主流程NavController
     */
    fun findMainNavController(view: View): NavController {
        val act = contextToActivity(view.context)
        if (act !is FragmentActivity) {
            throw RuntimeException("findMainNavController(v), $view context no FragmentActivity")
        }
        return findMainNavController(act)
    }

    /**
     * 获取主流程NavController
     */
    fun findMainNavController(activity: FragmentActivity): NavController {
        activity.supportFragmentManager.fragments.forEach {
            if (it is MainHostFragment) {
                return it.navController
            }
        }
        throw RuntimeException("MainHostfragment does not exist")
    }

    /**
     * 回到开始目的地，并判断是否打开新目的地
     */
    fun backStartDestination(navController: NavController, args: Bundle?, toDest: Dest?) {
        val startDest =
            Dest.Builder(navController.graph.startDestination).setArguments(args).build()
        toDest?.let {
            val dest = Dest.Builder(toDest).setPopUpTo(startDest, false).build()
            navController.launch(dest)
        } ?: kotlin.run {
            navController.launch(startDest)
        }

    }

    internal fun showSoftInput(view: View?,delayMillis:Long) {
        val v = view?.let { SoftReference(it) } ?: return
        val r = Runnable {
            v.get()?.let {
                if (!it.isAttachedToWindow) return@let
                if (!it.isFocused) {
                    if (!it.requestFocus()) return@Runnable
                }
                it.context.getSystemService(Context.INPUT_METHOD_SERVICE)?.let { imm ->
                    if (imm is InputMethodManager) {
                        imm.showSoftInput(view, 0)
                    }
                }
            }
        }
        if (delayMillis > 0) {
            view.postDelayed(r, delayMillis)
        } else {
            r.run()
        }
    }

    internal fun hideSoftInput(view: View) {
        hideSoftInputFragment = 0
        showSoftInputFragment = 0
        showSoftInputView = 0
        view.context.getSystemService(Context.INPUT_METHOD_SERVICE)?.let { imm ->
            if (imm is InputMethodManager) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    private fun contextToActivity(context: Context): Activity? {
        var act: Activity? = null
        var ctx: Context? = context
        do {
            when (ctx) {
                is Activity -> {
                    act = ctx
                }
                is ContextWrapper -> {
                    ctx = ctx.baseContext
                }
                else -> {
                    ctx = null
                }
            }
        } while (act == null && ctx != null)
        return act
    }

}