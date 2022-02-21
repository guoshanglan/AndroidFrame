package base2app.tread;/*
package com.zhuorui.securities.base2app.tread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

*/
/**
 * ExecutorsHelper
 *
 * @author Edward
 * @email miansheng.zheng@inin88.com
 * @company 深圳市盈通数据服务股份有限公司
 * <p>
 * @description 合规的 Executors 子线程创建辅助类
 * <p>
 * @date 2018-11-09
 *
 * @lastModifyDate 2019-01-20 12:13
 *//*

public class ExecutorsHelper {

    public static ExecutorService newCachedThreadPool(int maximumPoolSize) {
        return newThreadPoolExecutor(0, maximumPoolSize, 60L, TimeUnit.SECONDS);
    }

    public static ExecutorService newThreadPoolExecutor(int nThreads) {
        return newThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS);
    }

    public static ExecutorService newThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        // 设置线程命名空间: https://blog.csdn.net/w605283073/article/details/80259493
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-call-runner-%d").build();
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>(), namedThreadFactory);
    }
}*/
