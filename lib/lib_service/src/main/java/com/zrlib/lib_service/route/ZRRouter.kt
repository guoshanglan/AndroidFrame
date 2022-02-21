package com.zrlib.lib_service.route

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.zrlib.lib_service.route.ZRRouter.routeT
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap


/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/11/5
 * dest : ZRRouter
 */
object ZRRouter {

    /**
     * 这里是将动态代理生成的对象做了一个内存缓存 , 避免重复反射
     */
    private val mCache = ConcurrentHashMap<String, Any?>()

    @Suppress("UNCHECKED_CAST")
    fun <T> routeT(service: Class<T>): T? {
        var target:T? = mCache[service.name] as T
        if (target == null){
            target =  Proxy.newProxyInstance(service.classLoader, arrayOf<Class<*>>(service), object : InvocationHandler {
                override fun invoke(p0: Any?, method: Method?, p2: Array<out Any>?): Any? {
                    //获取方法所有的注解
                    val annotations = method?.annotations
                    val parameters: HashMap<String, String> = HashMap()
                    var voucher: Voucher? = null
                    if (annotations != null) {
                        for (i in annotations.indices) {
                            if (annotations[i] is ROUTE) { //如果注解是 ROUTE 类型
                                val annotation: ROUTE = annotations[i] as ROUTE
                                val url = annotation.path
                                //根据路由地址构造路由凭证
                                voucher = Voucher(ARouter.getInstance().build(url))
                                val parameterAnnotations = method.parameterAnnotations
                                //解析参数类型
                                if (p2 != null && parameterAnnotations.size == p2.size) {
                                    for (j in p2.indices) {
                                        var value: Any? = p2[j]
                                        if (value == null) continue
                                        value = if (value is Comparable<*>) {//如果只是基础数据 , 直接 put 在取时用 BundleEx 获取即可
                                            value.toString()
                                        } else {//非基础数据直接使用 gson 进行序列化
                                            Gson().toJson(value)
                                        }
                                        parameterAnnotations[j].forEach { p ->
                                            if (p is QUREY) {
                                                parameters[p.paramName] = value
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        voucher?.let {
                            if (parameters.isNotEmpty()) {
                                voucher.bundle = Bundle().apply {
                                    for (param in parameters) {
                                        putString(param.key, param.value)
                                    }
                                }
                                voucher.postcard = voucher.postcard.with(voucher.bundle)
                            }
                        }
                        return voucher
                    }
                    return null
                }

            }) as T
            mCache[service.name] = target
        }
        return target
    }

}

inline fun <reified T : Any> route(): T? {
    return routeT(T::class.java)
}