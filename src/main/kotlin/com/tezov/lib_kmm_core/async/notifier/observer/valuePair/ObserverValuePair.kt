


package com.tezov.lib_kmm_core.async.notifier.observer.valuePair

import com.tezov.lib_adr_core.async.notifier.observer.value.ObserverValue
import com.tezov.lib_kmm_core.type.primaire.Pair

abstract class ObserverValuePair<F : Any, S : Any>(owner: Any) :
    ObserverValue<Pair<F?, S?>?>(owner) {

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

}