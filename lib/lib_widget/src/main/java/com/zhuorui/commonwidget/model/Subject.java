package com.zhuorui.commonwidget.model;

/**
 * 观察者模式：目标对象模板
 *
 * @author PengXianglin
 */
public interface Subject<T> {

    // 注册订阅者
    void registerObserver(T... obs);

    // 移除订阅者
    void removeObserver(T... obs);

    //通知所有的观察者更新状态
    void notifyAllObservers();

}