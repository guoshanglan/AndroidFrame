package com.zhuorui.commonwidget.flow;

/**
 * 描述:  KVInterdace:KVInterdace
 */
public interface KVInterdace {

    /**
     * 无点击事件
     */
    int CLICK_NONE = 0;
    /**
     * 点击事件--打电话
     */
    int CLICK_MOBILE = 11;
    /**
     * 点击事件--查看物流
     */
    int CLICK_SHIPPING = 12;
    /**
     * 点击事件--复制
     */
    int CLICK_COPY = 13;
    /**
     * 布局模式--左右
     */
    int ORIENTATION_RL = 1;
    /**
     * 布局模式--上下
     */
    int ORIENTATION_TB = 2;
    /**
     * 布局模式--没有K
     */
    int ORIENTATION_NO_K = 4;
    /**
     * V数据格式--字符串
     */
    int V_FORMAT_STRING = 0;
    /**
     * V数据格式--json数组字符串
     */
    int V_FORMAT_ARRAY = 1;
    /**
     * 数据格式--json数组字符串,并两两一组展示
     */
    int V_FORMAT_ARRAY_GROUP = 2;
    /**
     * V数据类型--文本
     */
    int V_TYPE_TEXT = 0;
    /**
     * V数据类型--图片的URL
     */
    int V_TYPE_IMG = 1;
    /**
     * 对齐方式--两端对齐
     */
    int ITEM_ALIGN_JUSTIFY = 0;
    /**
     * 对齐方式--左对齐
     */
    int ITEM_ALIGN_LEFT = 1;
    /**
     * 对齐方式--居中
     */
    int ITEM_ALIGN_CENTER_HORIZONTAL = 2;
    /**
     * K数据类型--文本
     */
    int K_TYPE_TEXT = 0;
    /**
     * K数据类型--图片
     */
    int K_TYPE_IMG = 1;
    /**
     * K数据格式--文本
     */
    int K_FORMAT_TEXT = 0;
    /**
     * K数据格式--资源ID
     */
    int K_FORMAT_RESID = 1;


    /**
     * 获取title内容
     *
     * @return
     */
    Object getK();

    /**
     * 获取content内容
     *
     * @return
     */
    Object getV();

    /**
     * v文本的颜色
     *
     * @return
     */
    String getVColor();

    /**
     * k和v的布局结构
     *
     * @return
     */
    int getOrientation();

    /**
     * 点击事件
     *
     * @return
     */
    int getClick();


    /**
     * v的数据类型
     *
     * @return
     */

    int getVType();

    /**
     * v的数据格式
     *
     * @return
     */
    int getVFormat();

    /**
     * v的数据类型
     *
     * @return
     */
    int getKType();

    /**
     * K的数据格式
     *
     * @return
     */
    int getKFormat();

    /**
     * title文字样式
     *
     * @return
     */
    int getTitleTextStyle();

    /**
     * 内容文字样式
     *
     * @return
     */
    int getContentTextStyle();

    int getTitleImgWidth();

    int getTitleImgHight();

    int getItemAlign();

}
