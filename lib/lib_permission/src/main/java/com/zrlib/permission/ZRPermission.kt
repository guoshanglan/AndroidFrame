package com.zrlib.permission

import android.content.Intent
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.zrlib.permission.bean.Permission
import com.zrlib.permission.bean.Permissions
import com.zrlib.permission.callbcak.CheckRequestPermissionsListener
import com.zrlib.permission.callbcak.GoAppDetailCallBack
import com.zrlib.permission.debug.PermissionDebug

/**
 * author : liuwei
 * e-mail : vsanliu@foxmail.com
 * date   : 2020/10/12 15:12
 * desc   :
 */
class ZRPermission private constructor() : CheckRequestPermissionsListener, GoAppDetailCallBack {

    private var requestPermissionListeners: CheckRequestPermissionsListener? = null
    private var mPermissions: Permissions? = null
    private var refusedPermissions: Array<Permission>? = null
    private var dialog: PremissionDialog? = null
    private var beforeAllShowRequestPermissionRationale: Boolean = true//检查前，本次申请是否全部显示"不再询问"选择框

    /**
     * 检查运行时权限
     */
    fun checkRuntimePermission(permissions: Array<String>, listener: CheckRequestPermissionsListener) {
        checkRuntimePermission(permissions, listener, true)
    }

    /**
     * 检查运行时权限
     */
    fun checkRuntimePermission(permissions: Array<String>, listener: CheckRequestPermissionsListener, showDeniedDialog: Boolean) {
        PermissionDebug.setDebug(true)
        permissions.let {
            requestPermissionListeners = listener
            mPermissions = Permissions.build(*permissions)
            beforeAllShowRequestPermissionRationale =
                if (showDeniedDialog) {
                    shouldShowRequestPermissionRationale(mPermissions!!.permissions)
                } else {
                    true
                }
            PermissionUtil.getInstance().checkAndRequestPermissions(mPermissions!!, this)
        }
    }

    private fun shouldShowRequestPermissionRationale(permissions: Array<Permission>): Boolean {
        var t = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionUtil.getInstance().topActivity?.let { activity ->
                permissions.forEach {
                    t = t && activity.shouldShowRequestPermissionRationale(it.permissionName)
                }
            }
        }
        return t
    }

    /**
     * 所有权限ok，可做后续的事情
     *
     * @param allPermissions 权限实体类
     */
    override fun onAllPermissionOk(allPermissions: Array<Permission>) {
        requestPermissionListeners?.onAllPermissionOk(allPermissions)
        requestPermissionListeners = null
    }

    /**
     * 不ok的权限，被拒绝或者未授予
     *
     * @param refusedPermissions 权限实体类
     */
    override fun onPermissionDenied(refusedPermissions: Array<Permission>) {
        this.refusedPermissions = refusedPermissions
        val activity = PermissionUtil.getInstance().topActivity
        if (activity is FragmentActivity) {
            //检查后，被拒绝的权限下次申请是否全部显示"不再询问"选择框
            var afterAllShowRequestPermissionRationale = true
            if (!beforeAllShowRequestPermissionRationale) {
                //检查前，本次申请有不显示"不再询问"选择框，再检查被拒绝的权限下次申请是否全部显示"不再询问"选择框
                afterAllShowRequestPermissionRationale =
                    shouldShowRequestPermissionRationale(refusedPermissions)
            }
            if (!afterAllShowRequestPermissionRationale) {
                //被拒绝的权限下次申请有不显示"不再询问"选择框，弹出弹窗
                activity.supportFragmentManager.fragments.firstOrNull()?.let { f ->
                    dialog = PremissionDialog(f)
                    dialog?.let {
                        it.setOnClickBottomLeftViewListener { permissionDenied() }
                        it.setOnClickBottomRightViewListener {
                            PermissionUtil.getInstance().goApplicationSettings(this)
                        }
                    }
                    dialog?.setPremissions(refusedPermissions)
                    dialog?.show()
                }

                return
            }
        }
        permissionDenied()
    }

    private fun permissionDenied() {
        refusedPermissions?.let {
            requestPermissionListeners?.onPermissionDenied(it)
        }
        requestPermissionListeners = null
    }

    /**
     * 从App详情页回来
     *
     * @param data from onActivityResult
     */
    override fun onBackFromAppDetail(data: Intent?) {
        mPermissions?.let {
            PermissionUtil.getInstance().checkAndRequestPermissions(it, this)
        }
    }

    companion object {

        @Volatile
        private var mInstance: ZRPermission? = null
            get() {
                if (null == field) {
                    synchronized(PermissionUtil::class.java) {
                        if (field == null) {
                            field = ZRPermission()
                        }
                    }
                }
                return field
            }

        fun getInstance(): ZRPermission {
            return mInstance!!
        }
    }
}