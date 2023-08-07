


package com.tezov.lib_kmm_core.async.notifier.observer.state

import com.tezov.lib_kmm_core.async.notifier.observer.value.ObserverValueE

abstract class ObserverStateE(owner: Any) : ObserverValueE<Nothing?>(owner) {


    final override fun onComplete(value: Nothing?) {
        onComplete()
    }

    final override fun onException(value: Nothing?, exception: Throwable?) {
        onException(exception)
    }

    open fun onComplete() {
        throw NotImplementedError()
    }

    open fun onException(exception: Throwable?) {
        throw NotImplementedError()
    }

}