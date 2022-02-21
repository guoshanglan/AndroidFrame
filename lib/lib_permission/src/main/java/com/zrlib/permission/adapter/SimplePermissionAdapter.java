package com.zrlib.permission.adapter;


import com.zrlib.permission.bean.Permission;
import com.zrlib.permission.callbcak.CheckRequestPermissionListener;

/**
 * @author cd5160866
 */
public abstract class SimplePermissionAdapter implements CheckRequestPermissionListener {

    @Override
    public void onPermissionOk(Permission permission) {

    }

    @Override
    public void onPermissionDenied(Permission permission) {

    }
}
