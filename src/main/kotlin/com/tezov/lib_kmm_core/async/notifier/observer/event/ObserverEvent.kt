


package com.tezov.lib_kmm_core.async.notifier.observer.event

import com.tezov.lib_kmm_core.async.notifier.observable.ObservableEvent
import com.tezov.lib_kmm_core.async.notifier.observer.Observer

abstract class ObserverEvent<EVENT : Any, VALUE : Any?>(owner: Any, event: EVENT?) :
    Observer<EVENT, ObservableEvent.Packet<EVENT, VALUE>>(owner, event) {

    final override fun onChanged(packet: ObservableEvent.Packet<EVENT, VALUE>) {
        packet.event?.let { event ->
            if (isCanceled) {
                onCancel(event)
            } else {
                onComplete(event, packet.value)
            }
        } ?: run {
            throw IllegalStateException("can't be possible")
        }

    }

    open fun onCancel(event: EVENT) {
        throw NotImplementedError()
    }

    open fun onComplete(event: EVENT, value: VALUE) {
        throw NotImplementedError()
    }
}