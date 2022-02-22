package com.zhuorui.securties.debug.crashcanary.crash.db.dao;


import com.zhuorui.securties.debug.crashcanary.crash.db.CrashBean;

import java.util.List;


public interface CrashDao {


    /**
     * 添加
     */
    void addCrashBean(CrashBean crashBean);

    /**
     * 删除全部
     */
    void deleteALL();

    /**
     * 关闭表单
     */
    void closeSQL();

    /**
     * 查询一个
     */
    CrashBean queryCrashBeanById(int id);

    /**
     * 全查询
     */

    List<CrashBean> queryAll();

    /**
     * 删除一个
     */
    void delCrashBeanById(int id);
}
