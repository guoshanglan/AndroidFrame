package com.zrlib.matisse.intermal.entity;

import android.graphics.Color;

/**
 * author : liuwei
 * e-mail : vsanliu@foxmail.com
 * date   : 2020/9/10 14:48
 * desc   : 图片剪裁策略
 */
public class CutterStrategy {

    public static final int MASK_CIRCLE = 1;
    public static final int MASK_RECTANGLE = 0;

    public int maskStyle;
    public String ratio;
    public int maskColor;

    public CutterStrategy(int style) {
        this(style, "1:1");
    }

    public CutterStrategy(int style, String ratio) {
        this(style, ratio, Color.parseColor("#80000000"));
    }

    public CutterStrategy(int style, String ratio, int maskColor) {
        maskStyle = style;
        this.ratio = ratio;
        this.maskColor = maskColor;
    }


}
