package com.zrlib.permission.adapter;


import com.zrlib.permission.bean.Special;
import com.zrlib.permission.callbcak.SpecialPermissionListener;

/**
 * @author cd5160866
 */
public abstract class SimpleSpecialPermissionAdapter implements SpecialPermissionListener {

    @Override
    public void onDenied(Special permission) {

    }

    @Override
    public void onGranted(Special permission) {

    }
}
