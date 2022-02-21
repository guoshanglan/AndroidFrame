package base2app.network

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * BaseNetObserver
 *
 * @description
 * @date 2020/4/15 22:16
 */
abstract class BaseNetObserver<T> : Observer<T>{


    protected var disposable: Disposable? = null

    override fun onSubscribe(d: Disposable) {
        disposable = d
    }
    override fun onNext(t: T) {
        onResponse(t)
        disposable?.dispose()
    }

    override fun onError(e: Throwable) {
        onErrorCallback(e)
        disposable?.dispose()
    }

    override fun onComplete() {
    }

    abstract fun onErrorCallback(e: Throwable)

    abstract fun onResponse(t: T)

}