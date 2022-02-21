package com.zrlib.lib_service.route

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/11/5
 * dest : QUREY 路由入参注解
 */
@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class QUREY(val paramName: String)