package com.zhuorui.securties.debug

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.zhuorui.securties.debug.fps.FpsMonitor

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/11/16
 * dest : ZRDebugProvider
 * 用于自主注册debug相关配置逻辑
 */
class ZRDebugProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        (context?.applicationContext as Application).registerActivityLifecycleCallbacks(
            DebugDelegateHost()
        )
        Background.init(context?.applicationContext as Application)
        FpsMonitor.recordFps(true)
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