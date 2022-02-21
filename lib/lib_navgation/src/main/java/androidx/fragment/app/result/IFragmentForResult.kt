package androidx.fragment.app.result

import android.os.Bundle
import androidx.navigation.Dest

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/20 11:06
 *    desc   :
 */
interface IFragmentForResult {

    /**
     * 获取页面已发起返回值启动的参数
     */
    fun getFragmentForResultRequest():String?

    /**
     * 发起返回值启动
     */
    fun startFragmentForResult(requestCode: Int, dest: Dest)

    /**
     * 设置返回值启动结果
     */
    fun setResult(resultCode: Int, data: Bundle? = null)

    /**
     * 接收返回值启动结果
     */
    fun onFragmentForResult(requestCode: Int, resultCode: Int, data: Bundle?)

}