package com.zhuorui.commonwidget.model;

/**
 * @date 2020/7/23 18:00
 * @desc 索引类的汉语拼音的接口
 */

public abstract class BaseIndexPinyinBean extends BaseIndexBean {
    private String baseIndexPinyin;//城市的拼音

    public String getBaseIndexPinyin() {
        return baseIndexPinyin;
    }

    public BaseIndexPinyinBean setBaseIndexPinyin(String baseIndexPinyin) {
        this.baseIndexPinyin = baseIndexPinyin;
        return this;
    }

    //是否需要被转化成拼音，默认应该是需要的
    public boolean isNeedToPinyin() {
        return true;
    }

    //需要转化成拼音的目标字段
    public abstract String getTarget();

}
