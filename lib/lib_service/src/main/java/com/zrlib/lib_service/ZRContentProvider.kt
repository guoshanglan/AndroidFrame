package com.zrlib.lib_service

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.zrlib.lib_service.route.ZRAopManager

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/11/16
 * dest : ZRServiceProvider
 * 用于初始化service层的功能
 */
class ZRContentProvider: ContentProvider() {

    override fun onCreate(): Boolean {
        context?.let { ZRAopManager.init() }
        return true
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        return null
    }

    override fun getType(p0: Uri): String? {
        return null
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        return 0
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        return 0
    }
}