package base2app.util

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/8/9 09:46
 *    desc   : 主流程生命周期(统计实现IMain接口的activity)
 */
interface IMianLifecycle {

    /**
     * 主流程创建
     */
    fun mainCreated()

    /**
     * 主流程结束
     */
    fun mainDestroyed()
}