package base2app.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * author : liuwei
 * e-mail : vsanliu@foxmail.com
 * date   : 2020-03-27 14:47
 * desc   : rxJava网络请求回调，java代码调用,具体逻辑参考SubscribeCallback
 */
public abstract class JavaSubscribeCallback<T> implements Network.SubscribeCallback<T> {

    @Nullable
    @Override
    public String subErrorAccept(@NotNull Throwable t) {
        return null;
    }

    @Override
    public void onDoError() {
        onNetEnd();
    }

    @Override
    public void onDoOnDispose() {
        onNetEnd();
    }

    @Override
    public void onNetStart() {

    }

    @Override
    public void onNetEnd() {

    }

    @Override
    public boolean onBusinessFail(T response) {
        return false;
    }

    @Override
    public boolean onNetFailure(@NotNull ErrorResponse response) {
        return false;
    }

}
