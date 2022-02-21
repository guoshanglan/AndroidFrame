package com.zhuorui.commonwidget;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @date 2020/8/26 17:01
 * @desc TextWatcher基类，减少实现方法
 */
public class BaseTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
