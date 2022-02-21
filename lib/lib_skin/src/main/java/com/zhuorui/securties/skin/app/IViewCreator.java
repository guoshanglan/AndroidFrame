package com.zhuorui.securties.skin.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * date : 2021/04/16
 * dest : IViewCreator
 */
public interface IViewCreator {
    View createView(String name, Context context, AttributeSet attrs);
}
