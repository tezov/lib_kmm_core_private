


package com.tezov.lib_kmm_core.async.notifier

import com.tezov.lib_kmm_core.async.notifier.Notifier.Observable
import com.tezov.lib_kmm_core.async.notifier.Notifier.Packet
import com.tezov.lib_kmm_core.async.notifier.observer.Observer
import com.tezov.lib_kmm_core.extension.ExtensionIterable.remove
import com.tezov.lib_kmm_core.extension.ExtensionNull.isNotNull
import com.tezov.lib_kmm_core.extension.ExtensionNull.isNull
import com.tezov.lib_kmm_core.type.collection.ConcurrentLinkedSet
import com.tezov.lib_kmm_core.type.ref.Ref.Companion.value
import com.tezov.lib_kmm_core.type.ref.WR
import com.tezov.lib_kmm_core.util.UtilsNull.ifNotNull

internal typealias NotifierAny = Notifier<Any, Packet<Any>, Observable<Any, Packet<Any>>>
internal typealias ObserverAny = Observer<Any, Packet<Any>>

class Notifier<EVENT : Any, PACKET : Packet<EVENT>, OBSERVABLE : Observable<EVENT, PACKET>>(
    val observable: OBSERVABLE,
    notifyOnAddObserver: Boolean
) {

    init {
        observable.apply {
            @Suppress("UNCHECKED_CAST")
            attachTo(this@Notifier as NotifierAny)
        }
    }

    private var _notifyOnAddObserver = notifyOnAddObserver
    private var observers: ConcurrentLinkedSet<Observer<EVENT, PACKET>>? = null

    var notifyOnAddObserver: Boolean
        get() = synchronized(this) { _notifyOnAddObserver }
        set(value) {
            synchronized(this) { _notifyOnAddObserver = value }
        }

    fun obtainPacket(
        event: EVENT? = null
    ): PACKET = synchronized(this) { return this.observable.obtainPacket(event) }

    fun hasObserver(event: EVENT? = null): Boolean = synchronized(this) {
        val observers = this.observers ?: return false
        event?.let {
            return observers.find { it.event == event }.isNotNull
        } ?: kotlin.run {
            return observers.size > 0
        }
    }

    fun register(
        observer: Observer<EVENT, PACKET>,
        subscription: Subscription = Subscription(),
    ): Subscription = synchronized(this) {
        val observers = this.observers ?: kotlin.run {
            ConcurrentLinkedSet<Observer<EVENT, PACKET>>().also {
                this.observers = it
            }
        }
        observers.add(observer)
        @Suppress("UNCHECKED_CAST")
        subscription.bind(
            notifier = this as NotifierAny,
            observer = observer as ObserverAny,
        )
        if (_notifyOnAddObserver) {
            if (observer.event.isNotNull) {
                observable.getPacket(observer.event)?.takeIf { it.hasPendingEvent || subscription.isCanceled }?.let {
                    dispatchEvent(observer, it)
                }
            } else {
                for (packet in observable.getPacketList()) {
                    if (packet.hasPendingEvent || subscription.isCanceled) {
                        dispatchEvent(observer, packet)
                    }
                }
            }
        }
        return subscription
    }

    fun unregister(observer: Observer<EVENT, PACKET>) {
        synchronized(this) {
            val observers = this.observers ?: return
            observers.remove {
                it == observer
            }?.let {
                it.subscription?.let { subscription ->
                    subscription.unbind()
                    observer.unsubscribe()
                }
                if (observers.isEmpty()) {
                    this.observers = null
                }
            }
        }
    }

    private fun unregisterAll(observers:List<Observer<EVENT, PACKET>>){
        observers.forEach { observer ->
            observer.subscription?.let { subscription ->
                subscription.unbind()
                observer.unsubscribe()
            }
        }
        this.observers?.let {
            it.removeAll(observers)
            if (it.isEmpty()) {
                this.observers = null
            }
        }
    }

    fun unregisterAll(owner: Any) {
        synchronized(this) {
            observers?.filter {
                it.owner == owner || it.owner.isNull
            }?.takeIf { it.isNotEmpty() }?.let {
                unregisterAll(it)
            }
        }
    }

    fun unregisterAll(){
        synchronized(this) {
            this.observers?.let {
                unregisterAll(it.toList())
            }
        }
    }

    fun dispatchEvent(packet: PACKET) {
        synchronized(this) {
            val observers = this.observers ?: return
            if (!this.observable.isPacketValid(packet)) {
                throw IllegalStateException("Only observable who belong to this Notifier can notify observer")
            }
            observers.forEach {
                dispatchEvent(it, packet)
            }
        }
    }

    private fun dispatchEvent(observer: Observer<EVENT, PACKET>, packet: PACKET) {
        if (observer.owner.isNull) unregister(observer)
        else if ((observer.event == packet.event) || observer.event.isNull) {
            observer.notify(packet)
        }
    }

    interface Observable<EVENT : Any?, PACKET : Packet<EVENT>> {
        fun attachTo(notifier: NotifierAny)
        fun isPacketValid(packet: PACKET): Boolean
        fun getPacket(event: EVENT?): PACKET?
        fun obtainPacket(event: EVENT?): PACKET
        fun getPacketList(): List<PACKET>
        fun clearPacket()
    }

    interface Packet<EVENT : Any?> {
        val event: EVENT?
        val hasPendingEvent: Boolean
    }

    open class Subscription {
        private var notifierWR: WR<NotifierAny>? = null
        private var observerWR: WR<ObserverAny>? = null

        open var isBound = false
            protected set

        open var isCanceled = false
            protected set

        internal open fun bind(
            notifier: NotifierAny,
            observer: ObserverAny
        ) {
            if (isBound) throw IllegalStateException("subscription already bound")
            notifierWR = WR(notifier)
            observerWR = WR(observer)
            observer.bind(this)
            isBound = true
        }

        internal open fun unbind(){
            isBound = false
            notifierWR = null
            observerWR = null
        }

        open fun unsubscribe() {
            if(isBound){
                ifNotNull(notifierWR.value, observerWR.value) { notifier, observer ->
                    notifier.unregister(observer)
                    notifierWR = null
                    observerWR = null
                }
            }
        }

        open fun cancel() {
            isCanceled = true
        }

    }

}