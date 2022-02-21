package com.zhuorui.commonwidget.flow;

import androidx.annotation.Px;
import androidx.annotation.StyleRes;

import java.io.Serializable;

/**
 */

public class KV implements KVInterdace, Serializable {

    private Object k;//
    private Object v;//
    private String vFormat;////v的数据格式<string>(dfadsfdsfdf)<array>(["ffff","fff","fff"])
    private String vType;//v数据类型<txt>//文本<img>//图片url
    private String vColor;//"#ffffff"//内容文字的颜色，此字段对颜色控制权限高于contentTextStyle
    private String orientation;////k和v的布局结构<rl>左右<tb>上下<no_k>没有k
    private String onclick;//mobile/shipping

    // 以下字段服务端不定义，APP本地生成数据时才使用
    private String kFormat;//
    private String kType;//
    private int titleImgWidth;
    private int titleImgHight;
    private int titleTextStyle;//title文字的Style
    private int contentTextStyle;//content文字的Style
    private int itemAlign = -1;

    public KV() {
    }

    public KV(Object k, String v) {
        this.k = k;
        this.v = v;
    }

    public KV(Object k, CharSequence v) {
        this.k = k;
        this.v = v;
    }

    public KV(Object k, String v, String color) {
        this.k = k;
        this.v = v;
        this.vColor = color;
    }

    public KV(Object k, String v, @StyleRes int titleTextStyle, @StyleRes int contentTextStyle) {
        this.k = k;
        this.v = v;
        this.titleTextStyle = titleTextStyle;
        this.contentTextStyle = contentTextStyle;
    }

    @Override
    public Object getK() {
        return k;
    }

    @Override
    public Object getV() {
        return v;
    }

    @Override
    public int getVFormat() {
        return KVUtil.getVFormat(vFormat);
    }

    @Override
    public int getKType() {
        return KVUtil.getKType(kType);
    }

    @Override
    public int getKFormat() {
        return KVUtil.getKFormat(kFormat);
    }

    @Override
    public String getVColor() {
        return vColor;
    }

    @Override
    public int getOrientation() {
        return KVUtil.getOrientation(orientation);
    }

    @Override
    public int getClick() {
        return KVUtil.getOnclick(onclick);
    }

    @Override
    public int getVType() {
        return KVUtil.getVType(vType);
    }

    @Override
    public int getTitleTextStyle() {
        return titleTextStyle;
    }

    @Override
    public int getContentTextStyle() {
        return contentTextStyle;
    }

    @Override
    public int getTitleImgWidth() {
        return titleImgWidth;
    }

    @Override
    public int getTitleImgHight() {
        return titleImgHight;
    }

    @Override
    public int getItemAlign() {
        return itemAlign;
    }

    public void setK(Object k) {
        this.k = k;
    }

    public void setV(String v) {
        this.v = v;
    }

    public void setVColor(String vColor) {
        this.vColor = vColor;
    }

    public void setTextStyle(@StyleRes int titleStyle, @StyleRes int contentStyle) {
        titleTextStyle = titleStyle;
        contentTextStyle = contentStyle;
    }

    public void setTitleImgSize(@Px int width, @Px int hight) {
        titleImgWidth = width;
        titleImgHight = hight;
    }

    public void setVFormat(int format) {
        vFormat = KVUtil.getVFormatKey(format);
    }

    public void setVInfo(int type, int format) {
        vType = KVUtil.getVTypeKey(type);
        vFormat = KVUtil.getVFormatKey(format);
    }

    public void setKInfo(int type, int format) {
        kType = KVUtil.getKTypeKey(type);
        kFormat = KVUtil.getKFormatKey(format);
    }

    public void setOrientation(int or) {
        orientation = KVUtil.getOrientationKey(or);
    }

    public void setItemAlign(@FlowParamItemView.ItemAlignMode int itemAlign) {
        this.itemAlign = itemAlign;
    }
}
