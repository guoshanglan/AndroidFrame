package com.zrlib.permission.callbcak;


import com.zrlib.permission.bean.Special;

/**
 * @author cd5160866
 */
public interface SpecialPermissionListener {

    /**
     * 权限ok，可做后续的事情
     *
     * @param permission 权限实体类
     *                   {@link Special }
     */
    void onGranted(Special permission);

    /**
     * 权限不ok，被拒绝或者未授予
     *
     * @param permission 权限实体类
     */
    void onDenied(Special permission);

}
