package com.example.myframe.ui

import com.zhuorui.securities.base2app.ui.fragment.ZRPresenter

/**
 *    date   : 2019/8/16 16:07
 *    desc   : 主界面Presenter
 */
open class MainFragmentPresenter : ZRPresenter<MainFragmentView>() {



    /**
     * 延迟初始化
     */
    fun lazyInit() {
        addOpenAccountedAD()
    }


    /**
     * 添加开户引导
     */
    private fun addOpenAccountedAD() {

    }

    /**
     * 获取版本更新/弹窗数据
     */
    fun loadVersionAndPopData() {

    }

    override fun onDestory() {

        super.onDestory()
    }
}