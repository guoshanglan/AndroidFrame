package com.example.home.test.view

import android.view.View
import androidx.annotation.IntDef
import com.zhuorui.securities.base2app.ui.fragment.ZRView


/**
 *    date   : 2019/8/16 16:19
 *    desc   :主界面View
 */
interface MainFragmentView : ZRView {

    companion object {

        const val KEY_TYPE = "MainTabType"
        const val KEY_CHILD_DATA = "ChildData"
        const val KEY_IS_SHOW_DIALOG = "isShowDialog"
        const val KEY_CLEAR_FRAGMENT = "ClearFragment"

        /**
         * 市场
         */
        const val MARKET = 0

        /**
         * 交易/开户
         */
        const val TRANSACTION = 1

        /**
         * 发现
         */
        const val FIND = 2

        /**
         * 社区
         */
        const val COMMUNITY = 3

        /**
         * 我的
         */
        const val MINE = 4

        @IntDef(MARKET, TRANSACTION, FIND, COMMUNITY, MINE)
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        annotation class MainTab
    }

    /**
     * 是否正处于开户tab
     */
    fun inOpenAccountTab(): Boolean


}