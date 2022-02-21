package base2app.tread;/*
package com.zhuorui.securities.base2app.tread;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;


*/
/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/11/20
 * @description 基于 Android 的线程实现类
 * <p>
 * @date 2018-03-29
 * @lastModifyDate 2019-01-20 12:13
 *//*

class AppExecutorImpl implements BaseExecutor {

    private final Executor mSingleIo;

    private final Executor mComputationIO;

    private final Executor mNetworkIO;

    private final Executor mMainThread;

    AppExecutorImpl() {
        this(ExecutorsHelper.newThreadPoolExecutor(1), ExecutorsHelper.newCachedThreadPool(Runtime.getRuntime().availableProcessors() + 1),
                ExecutorsHelper.newThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1),
                new MainThreadExecutor()
        );
    }

    private AppExecutorImpl(Executor mSingleIo, Executor mComputationIO, Executor mNetworkIO, Executor mMainThread) {
        this.mSingleIo = mSingleIo;
        this.mComputationIO = mComputationIO;
        this.mNetworkIO = mNetworkIO;
        this.mMainThread = mMainThread;
    }

    @Override
    public void singleIO(Runnable command) {
        mSingleIo.execute(command);
    }

    @Override
    public void networkIO(Runnable command) {
        mNetworkIO.execute(command);
    }

    @Override
    public void computationIO(Runnable command) {
        mComputationIO.execute(command);
    }

    @Override
    public void mainThread(Runnable command) {
        mMainThread.execute(command);
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
*/
