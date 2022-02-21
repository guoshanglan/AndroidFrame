package com.zhuorui.commonwidget.model;


import com.zhuorui.commonwidget.impl.ISuspensionInterface;

/**
 * @date 2020/7/23 18:00
 * @desc 索引类的标志位的实体基类
 */
public abstract class BaseIndexBean implements ISuspensionInterface {
    private String baseIndexTag;//所属的分类（城市的汉语拼音首字母）

    public String getBaseIndexTag() {
        return baseIndexTag;
    }

    public BaseIndexBean setBaseIndexTag(String baseIndexTag) {
        this.baseIndexTag = baseIndexTag;
        return this;
    }

    @Override
    public String getSuspensionTag() {
        return baseIndexTag;
    }

    @Override
    public boolean isShowSuspension() {
        return true;
    }
}
