package com.zrlib.matisse.ui

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2020/8/18 12:57
 *    desc   :
 */
interface IPreviewItem {
    fun path(): String
    fun width(): Int
    fun height(): Int
    fun errRes():Int?{
        return null
    }
}