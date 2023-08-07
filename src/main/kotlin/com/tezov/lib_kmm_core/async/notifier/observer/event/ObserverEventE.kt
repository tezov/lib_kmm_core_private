


package com.tezov.lib_kmm_core.async.notifier.observer.event

import com.tezov.lib_kmm_core.async.notifier.observable.ObservableEventE
import com.tezov.lib_kmm_core.async.notifier.observer.Observer

abstract class ObserverEventE<EVENT : Any, VALUE : Any?>(owner: Any, event: EVENT?) :
    Observer<EVENT, ObservableEventE.Packet<EVENT, VALUE>>(owner, event) {

    final override fun onChanged(packet: ObservableEventE.Packet<EVENT, VALUE>) {
        packet.event?.let {event ->
            if (isCanceled) {
                onCancel(event)
            } else {
                packet.exception?.let { exception ->
                    onException(event, packet.value, exception)
                } ?: kotlin.run {
                    onComplete(event, packet.value)
                }
            }
        } ?: run {
            onException(null, packet.value, packet.exception)
        }
    }

    open fun onCancel(event: EVENT) {
        throw NotImplementedError()
    }

    open fun onComplete(event: EVENT, value: VALUE) {
        throw NotImplementedError()
    }

    open fun onException(event: EVENT?, value: VALUE?, exception: Throwable?) {
        throw NotImplementedError()
    }

}