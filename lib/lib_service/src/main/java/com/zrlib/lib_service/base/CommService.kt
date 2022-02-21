package com.zrlib.lib_service.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/6/23 15:40
 *    desc   : app公共服务
 */
class CommService private constructor() {

    private object SingletonHolder {
        val holder = CommService()
    }

    companion object {

        val instance = SingletonHolder.holder

        /**
         * 是否要进行非大陆登录操作
         */
        fun isNotCNIPLogin():Boolean{
            return instance.getNotCNIPLoginState().value?.isNotCNIPLogin() == true
        }
    }

    /**
     * 是否要进行非大陆IP登陆操作
     * 根据目前服务端约定以下条件判断条件
     * （http://192.168.1.203:3001/project/99/interface/api/145 code=900204）
     * （http://192.168.1.203:3001/project/27/interface/api/1663 code=000011）
     * （http://192.168.1.203:3001/project/27/interface/api/25 china=false）
     *
     */
    private val notCNIPLogin = MutableLiveData<IpHome>().apply { postValue(IpHome.valueOfHttp(false)) }

    /**
     * 网络连接状态监听
     * true:已接连
     */
    private val netState = MutableLiveData<Boolean>().apply { postValue(true) }

    /**
     * 保存网络连接状态
     */
    fun saveNetworkConnectState(connect: Boolean) {
        if (connect != netState.value) {
            netState.postValue(connect)
        }
    }

    /**
     * 获取网络连接状态LiveData
     */
    fun getNetworkConnectLiveData(): LiveData<Boolean> {
        return netState
    }

    /**
     * 获取是否要进行非大陆IP登录操作状态
     * true:需要进行非大陆IP登录 其他：不需要操作
     */
    fun getNotCNIPLoginState(): LiveData<IpHome> {
        return notCNIPLogin
    }

    /**
     * 设置是否要进行非大陆IP登录操作状态
     * @return true:需要进行非大陆登录操作 false:不进行操作
     */
    fun setNotCNIPLoginState(ipHome: IpHome):Boolean {
        if (ipHome != notCNIPLogin.value){
            notCNIPLogin.postValue(ipHome)
        }
        return ipHome.isNotCNIPLogin()
    }

}