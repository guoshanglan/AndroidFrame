@file:JvmName("ViewEx")
@file:JvmMultifileClass

package android.view

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.findFragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.*
import kotlin.reflect.KClass

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/6/25 10:36
 *    desc   :
 */

/**
 * 主流程导航
 */
val View.mainNav: NavController get() = NavUtil.findMainNavController(this)

/**
 * 获取View添加的Fragment
 */
val View.fragment: Fragment?
    get() {
        if (isAttachedToWindow) {
            try {
                return findFragment()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }

        }
        return null
    }

/**
 * 获取View所在fragment或Activity的LifecycleOwner
 */
val View.lifecycleOwner: LifecycleOwner?
    get() {
        return fragment ?: kotlin.run {
            if (isAttachedToWindow && context is FragmentActivity) {
                context as FragmentActivity
            } else {
                null
            }
        }
    }

/**
 * 启动Fragment
 */
@JvmOverloads
fun View.startFragment(kClass: KClass<*>, args: Bundle? = null, pop: Boolean? = false) {
    mainNav.launch(kClass, args, pop)
}

/**
 * 启动Fragment
 */
@JvmOverloads
fun View.startFragment(java: Class<*>, args: Bundle? = null, pop: Boolean? = false) {
    mainNav.launch(java, args, pop)
}

/**
 * 启动Fragment
 */
@JvmOverloads
fun View.startFragment(className: String, args: Bundle? = null, pop: Boolean? = false) {
    mainNav.launch(className, args, pop)
}

/**
 * 启动Fragment
 */
@JvmOverloads
fun View.startFragment(destinationId: Int, args: Bundle? = null, pop: Boolean? = false) {
    mainNav.launch(destinationId, args, pop)
}

/**
 * 启动Fragment
 */
fun View.startFragment(dest: Dest) {
    mainNav.launch(dest)
}

/**
 * 返回
 */
fun View.pop(): Boolean {
    return findNavController().pop()
}

/**
 * 返回指定页面
 */
fun View.popTo(dest: Dest, inclusive: Boolean): Boolean {
    return findNavController().popTo(dest, inclusive)
}

/**
 * 返回到首页
 * @param args 返回首页参数
 * @param toDest 返回首页打开的页面
 */
@JvmOverloads
fun View.backHome(args: Bundle? = null, toDest: Dest? = null) {
    NavUtil.backStartDestination(mainNav, args, toDest)
}

/**
 * 显示DialogFragment
 */
@JvmOverloads
fun View.showDialogFragment(java: Class<DialogFragment>, args: Bundle? = null) {
    findNavController().launch(java, args)
}

/**
 * 显示DialogFragment
 */
@JvmOverloads
fun View.showDialogFragment(kClass: KClass<DialogFragment>, args: Bundle? = null) {
    findNavController().launch(kClass, args)
}

/**
 * 显示DialogFragment
 */
@JvmOverloads
fun View.showDialogFragment(className: String, args: Bundle? = null) {
    findNavController().launch(className, args)
}

/**
 * 显示DialogFragment
 */
@JvmOverloads
fun View.showDialogFragment(destinationId: Int, args: Bundle? = null) {
    findNavController().launch(destinationId, args)
}

/**
 * 弹出键盘
 */
@JvmOverloads
fun View.showSoftInput(delayMillis:Long? = null) {
    fragment?.let {
        NavUtil.showSoftInputFragment = it.hashCode()
        NavUtil.showSoftInputView = hashCode()
    } ?: kotlin.run {
        if (NavUtil.showSoftInputFragment != 0 && NavUtil.showSoftInputView != hashCode()) {
            NavUtil.showSoftInputFragment = 0
            NavUtil.showSoftInputView = 0
        }
    }
    NavUtil.showSoftInput(this, delayMillis ?: 0)
}

/**
 * 隐藏键盘
 */
fun View.hideSoftInput() {
    if (isFocused){
        clearFocus()
    }
    NavUtil.hideSoftInput(this)
}