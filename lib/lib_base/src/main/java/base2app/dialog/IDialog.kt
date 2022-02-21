package base2app.dialog

import androidx.fragment.app.Fragment

/**

 * @desc Dialog接口
 */
interface IDialog {

    /**
     * 是否允许恢复
     */
    fun setCanRecover(allowRecover: Boolean): Any

    /**
     * dialog生命周期回调
     */
    fun setLifeCycle(lifeCycle: DialogLifeCycle): Any



    /**
     * 调用dialog的show方法
     */
    fun showDialog()

    /**
     * 获取当前绑定的fragment
     */
    fun getBindFragment(): Fragment?

    /**
     * 是否需要恢复
     */
    fun isCanRecover(): Boolean
}
