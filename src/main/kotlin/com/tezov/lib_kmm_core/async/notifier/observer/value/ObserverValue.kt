


package com.tezov.lib_adr_core.async.notifier.observer.value

import com.tezov.lib_kmm_core.async.notifier.observable.ObservableValue
import com.tezov.lib_kmm_core.async.notifier.observer.Observer

abstract class ObserverValue<VALUE : Any?>(owner: Any) :
    Observer<Nothing, ObservableValue.Packet<VALUE>>(owner, null) {

    final override fun onChanged(packet: ObservableValue.Packet<VALUE>) {
        if (isCanceled) {
            onCancel()
        } else {
            onComplete(packet.value)
        }
    }

    open fun onCancel() {
        throw NotImplementedError()
    }

    open fun onComplete(value: VALUE) {
        throw NotImplementedError()
    }

}