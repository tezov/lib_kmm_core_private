


package com.tezov.lib_kmm_core.async.notifier.observer.value

import com.tezov.lib_kmm_core.async.notifier.observable.ObservableValueE
import com.tezov.lib_kmm_core.async.notifier.observer.Observer

abstract class ObserverValueE<VALUE:Any?>(owner: Any) :
    Observer<Nothing, ObservableValueE.Packet<VALUE>>(owner, null) {

    final override fun onChanged(packet: ObservableValueE.Packet<VALUE>) {
        if (isCanceled) {
            onCancel()
        } else {
            packet.exception?.let {
                onException(packet.value, it)
            } ?: kotlin.run {
                onComplete(packet.value)
            }
        }
    }

    open fun onComplete(value: VALUE) {
        throw NotImplementedError()
    }

    open fun onException(value: VALUE?, exception: Throwable?) {
        throw NotImplementedError()
    }

    open fun onCancel() {
        throw NotImplementedError()
    }

}