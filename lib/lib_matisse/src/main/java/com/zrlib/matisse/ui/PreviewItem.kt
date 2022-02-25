package com.zrlib.matisse.ui

/**
 *    date   : 2020/8/18 13:03
 *    desc   :
 */
data class PreviewItem(val path: String, val w: Int, val h: Int, val errRes: Int? = null) :
    IPreviewItem {

    override fun path(): String {
        return path
    }

    override fun width(): Int {
        return w
    }

    override fun height(): Int {
        return h
    }

    override fun errRes(): Int? {
        return errRes
    }


}