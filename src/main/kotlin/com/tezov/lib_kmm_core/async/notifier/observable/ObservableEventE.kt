


package com.tezov.lib_kmm_core.async.notifier.observable

import com.tezov.lib_kmm_core.async.notifier.Notifier

class ObservableEventE<EVENT : Any, VALUE : Any?> :
    Observable<EVENT, ObservableEventE.Packet<EVENT, VALUE>>() {

    override fun createPacket(event: EVENT?): Packet<EVENT, VALUE> {
        return Packet(event, newDispatcher())
    }

    class Packet<EVENT : Any, VALUE : Any?>(
        event: EVENT?,
        dispatcher: (packet: Notifier.Packet<*>) -> Unit
    ) : ObservableEvent.Packet<EVENT, VALUE>(event, dispatcher) {

        private var _exception: Throwable? = null

        override val hasPendingEvent get() = super.hasPendingEvent || _exception != null

        override fun updateValue(value: Any?) {
            updateValue(value, null)
        }

        @Suppress("UNCHECKED_CAST")
        fun updateValue(value: Any?, exception: Throwable?) {
            this._exception = exception
            super.updateValue(value)
        }

        var exception: Throwable?
            get() = _exception
            set(value) {
                updateValue(null, value)
            }

        fun set(value: VALUE, exception: Throwable?) {
            updateValue(value, exception)
        }

        fun setIfDifferent(value: VALUE, exception: Throwable?) {
            if (value != this._value && exception !== this._exception) {
                updateValue(value, null)
            }
        }

    }
}