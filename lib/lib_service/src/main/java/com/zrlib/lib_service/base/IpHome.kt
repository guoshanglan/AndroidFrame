package com.zrlib.lib_service.base

import java.util.*

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/8/10 11:17
 *    desc   : ip信息
 */
class IpHome private constructor(login:Boolean?,source:Int){

    companion object{

        /**
         * IP信息来源退出等录
         */
        private const val SOURCE_LOGIN_OUT = 0

        /**
         * IP信息来源接口查询
         */
        private const val SOURCE_HTTP = 1

        /**
         * IP信息来源web socket认证
         */
        private const val SOURCE_SOCKET = 2

        /**
         * （http://192.168.1.203:3001/project/99/interface/api/145 code=900204）
         */
        fun valueOfHttp(isLogin:Boolean?):IpHome{
            return IpHome(isLogin,SOURCE_HTTP)
        }

        /**
         * （http://192.168.1.203:3001/project/27/interface/api/1663 code=000011）
         */
        fun valueOfSocket(isLogin:Boolean?):IpHome{
            return IpHome(isLogin,SOURCE_SOCKET)
        }

        /**
         * （http://192.168.1.203:3001/project/27/interface/api/25 china=false）
         */
        fun valueOfLoginOut(isLogin:Boolean?):IpHome{
            return IpHome(isLogin,SOURCE_LOGIN_OUT)
        }

        fun valueOfLogin():IpHome{
            return IpHome(null,-1)
        }
    }

    /**
     * 是否需要进行非中国大陆登录操作
     */
    private val notCNIPLogin:Boolean? = login

    /**
     * P信息来源
     */
    private val source:Int = source

    /**
     * 是否需要进行非中国大陆登录操作
     * @return true:需要
     */
    fun isNotCNIPLogin():Boolean{
        return notCNIPLogin == true
    }

    /**
     * 是否显示非中国大陆登录操作弹窗
     * @return true:显示
     */
    fun isShowNotCNIPLoginDialog():Boolean{
        return source != SOURCE_LOGIN_OUT
    }

    override fun hashCode(): Int {
        return Objects.hash(notCNIPLogin,source)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        val o = other as IpHome
        return notCNIPLogin == o.notCNIPLogin && source == o.source
    }
}