package com.zhuorui.commonwidget.impl;

/**
 * date   : 2019-08-28 10:31
 * desc   :
 */
public interface OnImageUploaderListener {
    void onFail(String code,String msg);

    void onSuccess(String url);
}
