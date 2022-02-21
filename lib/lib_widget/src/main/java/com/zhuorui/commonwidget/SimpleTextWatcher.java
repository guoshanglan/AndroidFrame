package com.zhuorui.commonwidget;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * 为了代码的简洁，定义了 SimpleTextWatcher
 */
public abstract class SimpleTextWatcher implements TextWatcher {

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