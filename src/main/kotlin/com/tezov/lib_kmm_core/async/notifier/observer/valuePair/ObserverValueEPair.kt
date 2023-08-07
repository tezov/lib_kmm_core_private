


package com.tezov.lib_kmm_core.async.notifier.observer.valuePair

import com.tezov.lib_kmm_core.async.notifier.observer.value.ObserverValueE
import com.tezov.lib_kmm_core.type.primaire.Pair

abstract class ObserverValueEPair<F : Any, S : Any>(owner: Any) :
    ObserverValueE<Pair<F?, S?>?>(owner) {


    final override fun onComplete(value: Pair<F?, S?>?) {
        value?.let {
            onComplete(value.first, value.second)
        } ?: kotlin.run {
            onComplete(null, null)
        }
    }

    open fun onComplete(first: F?, second: S?) {
        throw NotImplementedError()
    }

    final override fun onException(value: Pair<F?, S?>?, exception: Throwable?) {
        value?.let {
            onException(it.first, it.second, exception)
        } ?: kotlin.run {
            onException(null, null, exception)
        }
    }

    open fun onException(first: F?, second: S?, exception: Throwable?) {
        throw NotImplementedError()
    }

}