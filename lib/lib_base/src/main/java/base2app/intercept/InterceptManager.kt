

import android.app.Activity
import android.os.Bundle
import androidx.navigation.Dest
import com.alibaba.android.arouter.facade.Postcard
import base2app.BaseApplication
import base2app.ex.destination
import base2app.ex.logd
import base2app.ex.popStartTo
import base2app.intercept.IZRInterceptor
import com.zrlib.lib_service.ZRRouteInterceptorPriority.INTERCEPTOR_KEY
import com.zrlib.lib_service.route.ZRAopManager

/**
 * date : 2020/10/28
 * dest : ZRIntercepManager
 */
object InterceptManager {

    private val mInterceptors: MutableList<IZRInterceptor> = mutableListOf()

    fun registInterceptor(interceptor: IZRInterceptor) {
        mInterceptors.add(interceptor)
        mInterceptors.sortBy {//按照优先级进行排序处理
            it.getPriority()
        }
    }

    fun process(
        postcard: Postcard,
        bundle: Bundle?,
        action: ((Boolean, Dest?) -> Unit)?,
        befor: ((priority: Int, action: () -> Unit) -> Boolean)? = null
    ): Dest? {
        var priorty = ZRAopManager.interceptMap[postcard.path]
        logd("拦截的 路径${postcard.path} 以及对应优先级 $priorty")
        if (priorty == null)//如果默认拦截表中没有对应的优先级则再判断bundle中是否存在对应的的优先级 (唯一的可能是外部进行传入)
            priorty = bundle?.get(INTERCEPTOR_KEY)?.toString()?.toInt()

        if (mInterceptors.isNullOrEmpty() || priorty == null) {//如果拦截器或者当前跳转的拦截标记为空则直接走默认流程
            return postcard.destination(bundle)
        }
        doPprocess(priorty, {
            val fragment = postcard.destination(bundle)
            if (fragment == null) {
                BaseApplication.baseApplication.topActivity?.onBackPressed()
            } else {
                if (action == null) {
                    postcard.destination(bundle)?.let { dest->
                        popStartTo(dest)
                    }
                } else {
                    action.invoke(it, postcard.destination(bundle))
                }
            }
        }, befor)
        return null
    }

    /**
     *  @param priorty 用于处理对应的优先级
     *  @param action 最终处理的事件
     */
    private fun doPprocess(
        priorty: Int?,
        action: ((Boolean) -> Unit)?,
        befor: ((priority: Int, action: () -> Unit) -> Boolean)?
    ) {
        if (mInterceptors.isNullOrEmpty() || priorty == null) {//如果拦截器或者当前跳转的拦截标记为空则直接走默认流程
            action?.invoke(false)
        }
        val list = mutableListOf<IZRInterceptor>()
        for (i in mInterceptors.indices) {//对应这里的拦截器必须按照降序
            val interceptor = mInterceptors[i]
            if (interceptor.getPriority() > priorty!!) continue
            list.add(interceptor)
        }
        processInterceptors(list, action, befor)
    }

    /**
     * 逻辑套娃
     * @param interceptors 筛选过后拦截器集合
     * @param action 最终处理的事件
     */
    private fun processInterceptors(
        interceptors: MutableList<IZRInterceptor>,
        action: ((Boolean) -> Unit)?,
        befor: ((priority: Int, action: () -> Unit) -> Boolean)?
    ) {
        interceptors.removeLastOrNull()?.let { interceptor ->
            if (interceptors.isNullOrEmpty()) {
                interceptor.process(action, befor)
            } else {
                processInterceptors(interceptors, { isIntercept ->
                    if (isIntercept) {
                        BaseApplication.baseApplication.topActivity?.let {
                            it.onBackPressed()
                            (it as Activity).window.decorView.postDelayed({
                                interceptor.process(action, befor)
                            }, 300)
                        }
                    } else {
                        interceptor.process(action, befor)
                    }
                }, befor)
            }
        }
    }


    fun processWithAction(
        priorty: Int?,
        befor: ((priority: Int, action: () -> Unit) -> Boolean)? = null,
        delayMs:Long?=null,
        action: () -> Unit
    ) {
        doPprocess(priorty, { isIntercept ->
            try {
                if (isIntercept) {
                    BaseApplication.baseApplication.topActivity?.let {
                        if (delayMs == 0L) {
                            it.onBackPressed()
                            action.invoke()
                        } else {
                            it.onBackPressed()
                            (it as Activity).window.decorView.postDelayed({
                                action.invoke()
                            }, delayMs ?: 300)
                        }

                    }
                } else {
                    action.invoke()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, befor)
    }
}