package base2app.rxbus;

import com.jakewharton.rxrelay2.Relay;

import base2app.ex.LogExKt;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * Base bus
 * Created by threshold on 2017/1/18.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class BaseBus implements Bus {

    protected static final String TAG = "RxBus";

    /**
     * Set {@link EventThread#MAIN} {@link Scheduler} in your current environment.<br>
     * For example in Android,you probably set @{code AndroidSchedulers.mainThread()}.
     * <p>
     * Handy method for {@link EventThread#setMainThreadScheduler(Scheduler)}
     * </p>
     *
     * @param mainScheduler mainScheduler for {@link EventThread#MAIN}
     */
    public static void setMainScheduler(@NonNull Scheduler mainScheduler) {
        EventThread.setMainThreadScheduler(mainScheduler);
    }

    private Relay<Object> relay;

    public BaseBus(Relay<Object> relay) {
        this.relay = relay.toSerialized();
    }

    @Override
    public void post(@NonNull Object event) {
        ObjectHelper.requireNonNull(event, "event == null");
        if (hasObservers()) {
            LogExKt.logw("post event: %s", event);
            relay.accept(event);
        } else {
            LogExKt.logw("no observers,event will be discard: %s", event);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Observable<T> ofType(@NonNull Class<T> eventType) {
        if (eventType.equals(Object.class)) {
            return (Observable<T>) relay;
        }
        return relay.ofType(eventType);
    }

    @Override
    public boolean hasObservers() {
        return relay.hasObservers();
    }

    @Deprecated
    static class LoggerUtil {

        static void verbose(String message, Object... args) {
            LogExKt.logv(TAG, String.format(message, args));
        }

        static void debug(String message) {
            LogExKt.logd(TAG, message);
        }

        static void debug(Object msg) {
            LogExKt.logd(TAG, msg.toString());
        }

        static void debug(String message, Object... args) {
            LogExKt.logd(TAG, String.format(message, args));
        }

        static void info(String message, Object... args) {
            LogExKt.logi(TAG, String.format(message, args));
        }

        static void warning(String message, Object... args) {
            LogExKt.logw(TAG, String.format(message, args));
        }

        static void error(String message) {
            LogExKt.loge(TAG, message);
        }

        static void error(String message, Object... args) {
            LogExKt.loge(TAG, String.format(message, args));
        }

        static void error(Throwable throwable, String message, Object... args) {
            LogExKt.loge(TAG, String.format(message, args), throwable);
        }
    }
}