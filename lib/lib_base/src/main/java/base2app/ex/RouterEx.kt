package base2app.ex

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.startFragment
import androidx.navigation.Dest
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.launcher.ARouter
import base2app.BaseApplication
import base2app.ui.activity.AbsActivity
import com.zrlib.lib_service.route.Voucher


/**
 * date : 2020/10/28
 * dest : RouterEx 路由跳转相关扩展函数集合
 */
///**
// * 获取指定 Fragment，并传递参数
// */
//@Deprecated("请使用凭证获取 fragment")
//fun fragment(path: String, bundle: Bundle? = null): Fragment? {
//    val postcard = ARouter.getInstance().build(path)
//    bundle?.let {
//        postcard?.with(it)
//    }
//    val fragment = postcard.navigation() ?: return null
//    return fragment as Fragment
//}

/**
 * 获取指定 Fragment，并传递参数
 */
private fun fragmentDest(path: String, bundle: Bundle? = null): Dest? {
    val postcard = ARouter.getInstance().build(path)
    return postcard.destination(bundle)
}


/**
 * 当前方法是带操作类型的fragment操作 , 可以将 scheme 协议或者 arouter 协议进行传入且带关键事件拦截的方法
 * action 中 boolean 代表是否被拦截 , fragment 是你传入的path 或者 scheme uri 生成的 fragment
 * 我们可以在拦截之后自主进行跳转操作 ; 但是切记此时还是属于 拦截跳转的页面 并不是目的页面也是不事件发起页 ; 请留意!!!!
 */
fun fragment(path: String, bundle: Bundle? = null, action: ((Boolean, Fragment) -> Unit)? = null) {
    if (path.isEmpty()) return
    val postcard: Postcard
    var fragment: Fragment? = null
    if (path.isRouteUri()) {
        val uri = path.uri() ?: return
        fragment(uri, action)
    } else {
        postcard = ARouter.getInstance().build(path)
        bundle?.let {
            postcard?.with(it)
        }
        fragment = postcard.navigation() as Fragment?
        InterceptManager.process(postcard, bundle, { b, _ ->
            fragment?.let { action?.invoke(b, it) }
        })
    }
    fragment?.let {//如果没有进行拦截则进一步跳转
        action?.invoke(false, fragment)
    }
}

/**
 * 根据 scheme 路由协议进行跳转
 * zr://zrzq:8888/quotes/fragment/SimulationTradingMainFragment?login=10&tabIndex=2
 *  其中 /quotes/fragment/SimulationTradingMainFragment 为协议 path
 *  login=10&tabIndex=2 为入参 query
 *  zr://zrzq:8888 可用于后续合法校验逻辑 scheme://host:post
 */
fun fragment(path: Uri, action: ((Boolean, Fragment) -> Unit)? = null) {
    val parameters = path.parameters()
    var bundle: Bundle? = null
    if (!parameters.isNullOrEmpty()) {
        bundle = Bundle()
        for (param in parameters) {
            bundle.putString(param.key, param.value)
        }
    }
    path.path?.let { fragment(it, bundle, action) }
}

//---------------------------下面是专属 MAinActivity中 跳转单个 fragment 的逻辑---------------------------------------------

fun startTo(dest: Dest) {
    fragmentAction { act: FragmentActivity ->
        topFragment(act)?.startFragment(dest)
    }
}

/**
 * 关闭当前栈顶并跳转到对应的fragment中去
 */
fun popStartTo(dest: Dest) {
    fragmentAction { act: FragmentActivity ->
        topFragment(act)?.let {
            dest.className?.let { it1 -> it.startFragment(it1, dest.arguments, true) }
        }
    }
}


/**
 * 关闭到对应的 targetfragment 并打开新页面
 */
fun startPopToTarget(
    dest: Dest,
    targetFragmentClass: Class<out Fragment>,
    b: Boolean,
) {
    fragmentAction { act: FragmentActivity ->
        topFragment(act)?.let {
            val dest2 = Dest.Builder(dest)
                .setPopUpTo(targetFragmentClass.name, b).build()
            it.startFragment(dest2)
        }
    }
}

private fun fragmentAction(
    path: String,
    bundle: Bundle? = null,
    action: ((FragmentActivity, Dest) -> Unit)
) {
    BaseApplication.baseApplication.topActivity?.let { act ->
        fragmentDest(path, bundle)?.let {
            action(act, it)
        }
    }
}

fun fragmentAction(
    classF: Class<out Fragment>,
    bundle: Bundle?,
    action: (FragmentActivity, Dest) -> Unit
) {
    BaseApplication.baseApplication.topActivity?.let { act ->
        action(act, Dest.Companion.createDest(classF, bundle))
    }
}


private fun fragmentAction(action: ((FragmentActivity) -> Unit)) {
    BaseApplication.baseApplication.topActivity?.let { it ->
        action(it)
    }
}

private fun topFragment(activity: FragmentActivity): Fragment? {
    if (activity is AbsActivity) {
        return activity.topFragment
    }
    return null
}

//----------------------------- 根据路由凭证进行路由跳转 ------------------------------

fun Postcard.destination(bundle: Bundle?): Dest? {
    LogisticsCenter.completion(this)
    if (this.destination == null) {
        return null
    }
    return Dest.createDest(this.destination.name, bundle)
}

fun Voucher.destination(): Dest? {
    return postcard.destination(bundle)
}

fun Voucher.fragment(): Fragment? {
    var fragment: Fragment? = null
    InterceptManager.process(postcard, bundle, null)?.className?.let {
        kotlin.runCatching {
            fragment = Class.forName(it).getConstructor()
                .newInstance() as Fragment
        }
    }
    return fragment?.apply { arguments = bundle }
}

/**
 * 凭证的调用会在拦截逻辑判断结束后 执行 action 方法闭包
 * @param action.isIntercepted 为 true 代表是否有没有被拦截过 , 从业务逻辑来讲 , 一般都是以最后一个拦截器为标准
 * @param action.fragment 为对应目标页面
 */
fun Voucher.navigate(action: ((isIntercepted: Boolean, dest: Dest?) -> Unit)? = null) {
    val dest: Dest? = InterceptManager.process(postcard, bundle, action, befor)
    //如果没有进行拦截则进一步跳转
    action?.invoke(false, dest)
}


/**
 * 在凭证事件拦截之前作出逻辑穿插操作
 * @param befor 在凭证拦截前执行该操作 , 需要在自行处理完逻辑之后 invoke 对应方法的 befor.action 参数执行下一步 , 具体方法可以参照登录拦截弹窗的封装
 * @param befor.priority 返回参 , 代表着被拦截的事件 , 多用于判断被哪个拦截器拦截的判断 , 一般很少有业务会出现这类连续拦截连续弹窗的操作
 * @param befor.intercept 是对应拦截的后续动作 , 如果不进行 invoke 是无法继续执行下一步操作的
 */
fun Voucher.withIntercept(befor: (priority: Int, intercept: () -> Unit) -> Boolean): Voucher {
    this.befor = befor
    return this
}

//------------------下方都是已经包装好的闭包逻辑------------------

fun Voucher.startTo() {
    navigate { _, dest ->
        fragmentAction { act: FragmentActivity ->
            if (dest != null) {
                topFragment(act)?.startFragment(dest)
            }
        }
    }
}

/**
 * 关闭当前栈顶并跳转到对应的fragment中去
 */
fun Voucher.popStartTo() {
    navigate { _, dest ->
        fragmentAction { act: FragmentActivity ->
            dest?.let { topFragment(act)?.startFragment(it.className ?: "", it.arguments, true) }
        }
    }
}

/**
 * 关闭到对应的 targetfragment 并打开新页面
 */
fun Voucher.startPopToTarget(
    targetFragmentClass: Class<out Fragment>,
    b: Boolean,
) {
    navigate { _, dest ->
        fragmentAction { act: FragmentActivity ->
            topFragment(act)?.let {
                val destResult = dest?.let { it1 ->
                    Dest.Builder(it1)
                        .setPopUpTo(targetFragmentClass.name, b).build()
                }
                if (destResult != null) {
                    it.startFragment(destResult)
                }
            }
        }
    }
}
