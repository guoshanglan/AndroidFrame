package com.zrlib.lib_service.route

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/11/5
 * dest : ROUTE 路由 path 注解
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ROUTE(val path: String)