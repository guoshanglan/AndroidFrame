@file:JvmName("FragmentEx")
@file:JvmMultifileClass

package androidx.fragment.app

import android.os.Bundle
import android.os.Looper
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.navigation.*
import androidx.navigation.fragment.findNavController
import kotlin.reflect.KClass

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/6/25 10:03
 *    desc   : 在Fragment使用导航相关方法
 */

/**
 * 主流程导航
 */
val Fragment.mainNav: NavController get() = NavUtil.findMainNavController(this)

/**
 * Fragment所在层级内导航
 */
val Fragment.currentNav: NavController get() = this.findNavController()

/**
 * 启动Fragment
 */
@JvmOverloads
fun Fragment.startFragment(kClass: KClass<*>, args: Bundle? = null, pop: Boolean? = false) {
    getDefNav().launch(kClass, args, pop)
}

/**
 * 启动Fragment
 */
@JvmOverloads
fun Fragment.startFragment(java: Class<*>, args: Bundle? = null, pop: Boolean? = false) {
    getDefNav().launch(java, args, pop)
}

/**
 * 启动Fragment
 */
@JvmOverloads
fun Fragment.startFragment(className: String, args: Bundle? = null, pop: Boolean? = false) {
    getDefNav().launch(className, args, pop)
}

/**
 * 启动Fragment
 */
@JvmOverloads
fun Fragment.startFragment(destinationId: Int, args: Bundle? = null, pop: Boolean? = false) {
    getDefNav().launch(destinationId, args, pop)
}

/**
 * 启动Fragment
 */
fun Fragment.startFragment(dest: Dest) {
    getDefNav().launch(dest)
}

/**
 * 返回
 */
fun Fragment.pop(): Boolean {
    return getDefNav().pop()
}

/**
 * 返回指定页面
 */
fun Fragment.popTo(dest: Dest, inclusive: Boolean): Boolean {
    return getDefNav().popTo(dest, inclusive)
}

/**
 * 返回到首页
 * @param args 返回首页参数
 * @param toDest 返回首页打开的页面
 */
@JvmOverloads
fun Fragment.backHome(args: Bundle? = null, toDest: Dest? = null) {
    if (Thread.currentThread() === Looper.getMainLooper().thread) {
        NavUtil.backStartDestination(mainNav, args, toDest)
    } else {
        activity?.runOnUiThread {
            NavUtil.backStartDestination(mainNav, args, toDest)
        }
    }
}

/**
 * 显示DialogFragment
 */
@JvmOverloads
fun Fragment.showDialogFragment(java: Class<*>, args: Bundle? = null) {
    currentNav.launch(java, args)
}

/**
 * 显示DialogFragment
 */
@JvmOverloads
fun Fragment.showDialogFragment(kClass: KClass<*>, args: Bundle? = null) {
    currentNav.launch(kClass, args)
}

/**
 * 显示DialogFragment
 */
@JvmOverloads
fun Fragment.showDialogFragment(className: String, args: Bundle? = null) {
    currentNav.launch(className, args)
}

/**
 * 显示DialogFragment
 */
@JvmOverloads
fun Fragment.showDialogFragment(destinationId: Int, args: Bundle? = null) {
    currentNav.launch(destinationId, args)
}

/**
 * 通过Tag查找childFragmentManager下的Fragment
 */
fun Fragment.findChildFragment(tag: String?): Fragment? {
    if (tag == null) return null
    return childFragmentManager.findFragmentByTag(tag)
}

/**
 * 通过calss查找childFragmentManager下的Fragment
 */
fun Fragment.findChildFragment(java: Class<*>): Fragment? {
    return findChildFragment(java.name)
}

/**
 * 通过calss查找childFragmentManager下的Fragment
 */
fun Fragment.findChildFragment(kClass: KClass<*>): Fragment? {
    return findChildFragment(kClass.java.name)
}

/**
 * 替换Fragment
 */
fun Fragment.replaceFragment(@IdRes containerId: Int, fragment: Fragment, tag: String? = null) {
    if (fragment.isAdded) return
    val realTag = tag ?: fragment.javaClass.name
    childFragmentManager.beginTransaction().let {
        it.replace(containerId, fragment, realTag)
        it.commitNowAllowingStateLoss()
    }
}


/**
 * 切换Fragment，按需加载
 * @return 第一次添加调用invoke生成Fragment添加，此时返回null。如果Fragment已经添加过，返回被重新显示Fragment
 */
fun Fragment.switchFragment(
    @IdRes containerId: Int,
    tag: String,
    fragment: ((tag: String) -> Fragment)
): Fragment? {
    return switchFragment(containerId, tag, false, fragment)
}

/**
 * 切换Fragment，按需加载
 * @return 第一次添加调用invoke生成Fragment添加，此时返回null。如果Fragment已经添加过，返回被重新显示Fragment
 */
fun Fragment.switchFragment(
    @IdRes containerId: Int,
    tag: String,
    returnNew: Boolean? = false,
    fragment: ((tag: String) -> Fragment)
): Fragment? {
    val fm = childFragmentManager
    val ft: FragmentTransaction = fm.beginTransaction()
    val findFragment = fm.findFragmentByTag(tag)?.also {
        if (it.isDetached) {
            ft.attach(it)
        }
    } ?: kotlin.run {
        val temp = fragment.invoke(tag)
        ft.add(containerId, temp, tag)
        if (returnNew == true) {
            temp
        } else {
            null
        }

    }
    fm.fragments.forEach {
        if (it.id == containerId && it.tag != tag && !it.isDetached) {
            ft.detach(it)
        }
    }
    ft.commitNowAllowingStateLoss()
    return findFragment
}

/**
 * 切换Fragment.一次加载全部Fragment
 */
@JvmOverloads
fun Fragment.switchMultipleFragment(
    @IdRes containerId: Int,
    showFragment: Fragment,
    tags: Array<String>? = null,
    fragments: (() -> Array<Fragment>),
) {
    val fm = childFragmentManager
    val ft: FragmentTransaction = fm.beginTransaction()
    val addedFragments = fm.fragments
    if (!addedFragments.contains(showFragment)) {
        fragments.invoke().forEachIndexed { i, f ->
            val tag = tags?.get(i) ?: f.javaClass.name
            ft.add(containerId, f, tag)
            if (showFragment != f) {
                ft.hide(f)
            }
        }
    } else {
        addedFragments.forEach {
            if (it.id == containerId) {
                if (it == showFragment) {
                    ft.show(it)
                } else {
                    ft.hide(it)
                }
            }
        }
    }
    ft.commitNowAllowingStateLoss()
}

/**
 * 移除fragment
 */
fun Fragment.removeFragment() {
    if (isAdded) {
        parentFragmentManager.beginTransaction().let {
            it.remove(this)
            it.commitNowAllowingStateLoss()
        }
    }
}

/**
 * 弹出键盘
 */
@JvmOverloads
fun Fragment.showSoftInput(view: EditText?, delayMillis: Long? = null) {
    NavUtil.showSoftInputFragment = this.hashCode()
    NavUtil.showSoftInputView = view?.hashCode() ?: 0
    NavUtil.showSoftInput(view, delayMillis ?: 0)
}

/**
 * 隐藏键盘
 */
fun Fragment.hideSoftInput() {
    view?.let {
        NavUtil.hideSoftInput(it)
    }
}

/**
 * 获取默认Nav
 */
private fun Fragment.getDefNav(): NavController {
    return when (this) {
        is IDefaultNav -> {
            getDefaultNav()
        }
        is NavHost -> {
            //当前触发的fragment是NavHost，则用main流程执行
            mainNav
        }
        else -> {
//            var f: Fragment? = this
//            while (f != null && f !is NavHost) {
//                f = f.parentFragment
//                //TabHostFragment 默认操作执行在main流程
//                if (f is TabHostFragment) {
//                    return mainNav
//                }
//            }
            currentNav
        }
    }
}
