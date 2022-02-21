package com.zrlib.lib_service.route

import java.util.*

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/11/13
 * dest : ZRInterceptManager
 */
object ZRAopManager {

    val interceptMap: Map<String, Int> = HashMap()

    fun init() {
        try {
          registAction()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registAction() {
    }

    /**
     * 注册拦截数据类
     */
//    private fun registInterceptor(className: String) {
//        if (className.startsWith("${Constant.PACKAGE}.${Constant.MODULE_HEAD}")) {
//            (Class.forName(className).getConstructor()
//                .newInstance() as ZRInterceptInitializer).regist(interceptMap)
//        }
//    }
}