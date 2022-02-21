@file:JvmName("Nav")
@file:JvmMultifileClass

package androidx.navigation

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentFactory
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import kotlin.reflect.KClass


/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/6/25 10:17
 *    desc   : 导航工具
 */

/**
 * 打开日志
 */
fun Application.openNavLog() {
    NavUtil.openDebug()
}

fun Application.navInit(){
    DestIdSP.instance.init(this)
}

@JvmOverloads
fun NavController.launch(
    java: Class<*>,
    args: Bundle? = null,
    pop: Boolean? = false
) {
    val builder = Dest.Builder(java).setArguments(args)
    if (pop == true) {
        builder.setPopUpTo(currentDestination?.id ?: 0, true)
    }
    launch(builder.build())
}

@JvmOverloads
fun NavController.launch(
    kClass: KClass<*>,
    args: Bundle? = null,
    pop: Boolean? = false
) {
    val builder = Dest.Builder(kClass).setArguments(args)
    if (pop == true) {
        builder.setPopUpTo(currentDestination?.id ?: 0, true)
    }
    launch(builder.build())
}

@JvmOverloads
fun NavController.launch(
    className: String,
    args: Bundle? = null,
    pop: Boolean? = false
) {
    val builder = Dest.Builder(className).setArguments(args)
    if (pop == true) {
        builder.setPopUpTo(currentDestination?.id ?: 0, true)
    }
    launch(builder.build())
}

@JvmOverloads
fun NavController.launch(
    destinationId: Int,
    args: Bundle? = null,
    pop: Boolean? = false
) {
    val builder = Dest.Builder(destinationId).setArguments(args)
    if (pop == true) {
        builder.setPopUpTo(currentDestination?.id ?: 0, true)
    }
    launch(builder.build())
}

fun NavController.launch(dest: Dest,pop: Boolean) {
    val d = if (pop) {
        Dest.Builder(dest).setPopUpTo(currentDestination?.id ?: 0, true).build()
    } else {
        dest
    }
    launch(d)
}

fun NavController.launch(dest: Dest) {
    val toClsName = dest.getDestinationClassName(this) ?: return
    val toCls = FragmentFactory.loadFragmentClass(context.classLoader, toClsName)
    when {
        //启动的目的地是Single
        SingleDestiantion::class.java.isAssignableFrom(toCls) -> {
            if (isAddBackStack(toClsName)) {
                dest.arguments?.let { value ->
                    SingleDestiantion.setFragmentResult(context, toClsName, value)
                }
                val op = dest.toAnimOptionsBuilder().setPopUpTo(requireDestinationId(dest), false)
                    .build()
                navigate(0, null, op)
            } else {
                val op = dest.toAnimOptionsBuilder().build()
                navigate(requireDestinationId(dest), dest.arguments, op)
            }
            return
        }
        //返回到指定目的地，再启动新目的地
        dest.popDest != null -> {
            val popDest = dest.popDest
            val op = popDest.getDestinationClassName(this)?.let { className ->
                if (isAddBackStack(className)) className else null
            }?.let { popClsName ->
                val popCls = FragmentFactory.loadFragmentClass(context.classLoader, popClsName)
                //需要返回的目的地是Single
                if (SingleDestiantion::class.java.isAssignableFrom(popCls)) {
                    if (!dest.popInclusive) {
                        popDest.arguments?.let { value ->
                            SingleDestiantion.setFragmentResult(context, popClsName, value)
                        }
                    }
                    dest.toAnimOptionsBuilder()
                        .setPopUpTo(requireDestinationId(popDest), dest.popInclusive)
                        .build()
                } else {
                    null
                }
            } ?: dest.toNavOptions()
            navigate(
                requireDestinationId(dest),
                dest.arguments,
                op,
                dest.extras
            )
            return
        }
        else -> {
            navigate(
                requireDestinationId(dest),
                dest.arguments,
                dest.toNavOptions(),
                dest.extras
            )
        }
    }
}

/**
 * 返回
 * @param activityBackPressed NavController返回没有成功是否调用Activity.onBackPressed false：不调用
 * @return true:返回成功执行，false:没有成功执行
 */
@JvmOverloads
fun NavController.pop(activityBackPressed: Boolean? = true): Boolean {
    val t = navigateUp()
    if (!t && activityBackPressed != false) {
        (context as Activity).onBackPressed()
        return true
    }
    return t
}

/**
 * 返回到指定目的地
 * @param dest 目的地
 * @param inclusive 是否包含返回的目的地
 * @return true:返回成功执行，false:没有成功执行
 */
@JvmOverloads
fun NavController.popTo(dest: Dest, inclusive: Boolean): Boolean {
    val toClsName = dest.getDestinationClassName(this) ?: return false
    if (isAddBackStack(toClsName)) {
        launch(Dest.Builder(0).setPopUpTo(dest, inclusive).build())
        return true
    }
    return false
}

/**
 * 获取返回栈内目地的数量
 */
val NavController.backStackSize: Int
    get() = mBackStack.size - 1//导航图根会占一个位置，要减去

/**
 * 是否添加到返回栈
 */
private fun NavController.isAddBackStack(className: String): Boolean {
    mBackStack.descendingIterator().forEach {
        val dest = it.destination
        if (dest is FragmentNavigator.Destination && className == dest.className) {
            return true
        }
    }
    return false
}

/**
 * 查找class的DestinationId
 */
private fun NavController.findDestinationId(java: Class<*>): Int {
    return findDestinationId(java.name)
}

/**
 * 查找class的DestinationId
 */
private fun NavController.findDestinationId(kClass: KClass<*>): Int {
    return findDestinationId(kClass.java.name)
}

/**
 * 查找className的DestinationId
 */
private fun NavController.findDestinationId(className: String): Int {
    return graph.findDestinationId(className)
}

private fun NavGraph.findDestinationId(className: String):Int{
    iterator().forEach {
        if (it is FragmentNavigator.Destination) {
            if (it.className == className) {
                return it.id
            }
        } else if (it is DialogFragmentNavigator.Destination) {
            if (it.className == className) {
                return it.id
            }
        }
    }
    return NavUtil.createDestinationId(className)
}

/**
 * 请求目的地在导航图中Id，导航图中不存在目地的则新增
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal fun NavController.requireDestinationId(className: String): Int {
    return graph.requireDestinationId(this,className)
}

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal fun NavGraph.requireDestinationId(navController:NavController,className:String):Int{
    val destId = findDestinationId(className)
    if (findNode(destId) != null) return destId
    val cls = FragmentFactory.loadFragmentClass(navController.context.classLoader, className)
    val destination: NavDestination = when {
        DialogFragment::class.java.isAssignableFrom(cls) -> {
            navController.navigatorProvider.getNavigator<Navigator<DialogFragmentNavigator.Destination>>("dialog")
                .createDestination()
                .also {
                    it.id = destId
                    it.className = className
                }
        }
        else -> {
            navController.navigatorProvider.getNavigator<Navigator<FragmentNavigator.Destination>>("fragment")
                .createDestination().also {
                    it.id = destId
                    it.className = className
                }
        }
    }
    addDestination(destination)
    return destId

}

/**
 * 请求目的地在导航图中Id，导航图中不存在目地的则新增
 */
private fun NavController.requireDestinationId(dest: Dest): Int {
    return dest.destId.let {
        if (it == 0) {
            requireDestinationId(dest.className!!)
        } else {
            it
        }
    }
}

/**
 * 获取导航图
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal fun NavController.navGraph(): NavGraph? {
    return mGraph
}
