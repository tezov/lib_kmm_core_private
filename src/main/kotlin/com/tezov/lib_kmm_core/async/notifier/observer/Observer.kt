


package com.tezov.lib_kmm_core.async.notifier.observer

import com.tezov.lib_kmm_core.async.notifier.Notifier.Packet
import com.tezov.lib_kmm_core.async.notifier.Notifier.Subscription
import com.tezov.lib_kmm_core.extension.ExtensionBoolean.isTrueOrNull
import com.tezov.lib_kmm_core.type.ref.WR

abstract class Observer<EVENT : Any, PACKET : Packet<EVENT>>(owner: Any, val event: EVENT?) {
    private val ownerWR: WR<Any> = WR(owner)
    var subscription: Subscription? = null
        private set

    var isEnabled = true

    val owner get() = ownerWR.value

    fun bind(subscription: Subscription){
        this.subscription = subscription
    }

    fun unsubscribe() {
        subscription?.let {
            subscription = null
            it.unsubscribe()
        }
    }

    val isCanceled get() = subscription?.let { it.isCanceled || !it.isBound }.isTrueOrNull

    fun notify(packet: PACKET) {
        if (isEnabled) {
            onChanged(packet)
        }

    }

    abstract fun onChanged(packet: PACKET)

}