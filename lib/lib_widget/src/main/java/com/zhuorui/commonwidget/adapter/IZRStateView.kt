package com.zhuorui.commonwidget.adapter

import android.view.View
import androidx.annotation.IntDef
import com.zhuorui.commonwidget.ZRMultiStateFrame
import java.lang.annotation.ElementType.*
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 *    date   : 2020/6/12 11:51
 *    desc   :
 */
interface IZRStateView {


    companion object {

        /** 加载中*/
        const val LAODING = 1

        /** 加载失败*/
        const val FAIL = 2

        /** 空占位*/
        const val EMPTY = 3

        /** 自定义占位*/
        const val CUSTOM = 4

        /** 内容视图*/
        const val CONTENT = 5


        @IntDef(CONTENT, LAODING, FAIL, EMPTY, CUSTOM)
        @Retention(RetentionPolicy.CLASS)
        @Target(METHOD, PARAMETER, FIELD, LOCAL_VARIABLE)
        annotation class ZRSVState

    }

    /**
     * 获取状态View
     */
    fun getView(): View

    /**
     * 设置状态帧
     */
    fun setFrame(frame: ZRMultiStateFrame)

}