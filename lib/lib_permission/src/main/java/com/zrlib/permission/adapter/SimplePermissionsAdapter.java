package com.zrlib.permission.adapter;


import com.zrlib.permission.bean.Permission;
import com.zrlib.permission.callbcak.CheckRequestPermissionsListener;

/**
 * @author cd5160866
 */
public abstract class SimplePermissionsAdapter implements CheckRequestPermissionsListener {
    @Override
    public void onAllPermissionOk(Permission[] allPermissions) {

    }

    @Override
    public void onPermissionDenied(Permission[] refusedPermissions) {

    }
}
