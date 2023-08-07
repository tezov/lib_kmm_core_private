


package com.tezov.lib_kmm_core.async.notifier.observable

import com.tezov.lib_kmm_core.async.notifier.Notifier
import com.tezov.lib_kmm_core.util.UtilsNull.NULL


open class ObservableValue<VALUE:Any?> : Observable<Nothing, ObservableValue.Packet<VALUE>>() {

    override fun createPacket(event: Nothing?): Packet<VALUE> {
        return Packet(newDispatcher())
    }

    open class Packet<VALUE:Any?> (
        val dispatcher: (packet: Notifier.Packet<*>) -> Unit
    ): Notifier.Packet<Nothing> {

        override val event: Nothing? get() = null

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
                if(value != this._value){
                    updateValue(value)
                }
            }
    }
}