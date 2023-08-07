


package com.tezov.lib_kmm_core.async.notifier.observable

import com.tezov.lib_kmm_core.async.notifier.Notifier
import com.tezov.lib_kmm_core.util.UtilsNull.NULL

open class ObservableEvent<EVENT : Any, VALUE : Any?> :
    Observable<EVENT, ObservableEvent.Packet<EVENT, VALUE>>() {

    override fun createPacket(event: EVENT?): Packet<EVENT, VALUE> {
        return Packet(event, newDispatcher())
    }

    open class Packet<EVENT : Any, VALUE : Any?>(
        override val event: EVENT?,
        val dispatcher: (packet: Notifier.Packet<*>) -> Unit
    ) : Notifier.Packet<EVENT> {

        protected var _value: Any? = NULL

        override val hasPendingEvent get() = _value != NULL

        @Suppress("UNCHECKED_CAST")
        protected open fun updateValue(value: Any?) {
            this._value = value as? VALUE
            dispatcher(this)
        }

        @Suppress("UNCHECKED_CAST")
        open var value: VALUE
            get() = _value?.takeIf { it != NULL } as VALUE
            set(value) {
                updateValue(value)
            }

        @Suppress("UNCHECKED_CAST")
        open var valueIfDifferent: VALUE
            get() = value
            set(value) {
                if (value != this._value) {
                    updateValue(value)
                }
            }
    }
}