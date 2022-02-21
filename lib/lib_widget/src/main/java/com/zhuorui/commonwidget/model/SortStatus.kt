package com.zhuorui.commonwidget.model

/**
 * @date 2020/4/24 14:15
 * @desc 排序状态枚举类，UP:大->小，DOWN:小->大，NORMAL:默认
 */
enum class SortStatus(val value: Int) {
    NORMAL(1), UP(2), DOWN(3),
}