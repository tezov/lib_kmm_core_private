


package com.tezov.lib_kmm_core.async.notifier.observer.state

import com.tezov.lib_adr_core.async.notifier.observer.value.ObserverValue

abstract class ObserverState(owner: Any) : ObserverValue<Nothing?>(owner) {

    final override fun onComplete(value: Nothing?) {
        onComplete()
    }

    open fun onComplete() {
        throw NotImplementedError()
    }
}