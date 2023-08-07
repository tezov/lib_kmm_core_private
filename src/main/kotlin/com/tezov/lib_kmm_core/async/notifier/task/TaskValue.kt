


package com.tezov.lib_kmm_core.async.notifier.task

import com.tezov.lib_kmm_core.async.notifier.observable.ObservableValueE

open class TaskValue<VALUE:Any?> :
    Task<Nothing, ObservableValueE.Packet<VALUE>, ObservableValueE<VALUE>>(null, ObservableValueE()) {

    fun notifyComplete(value: VALUE) {
        notifier.dispatchEvent(obtainPacket().apply {
            this.value = value
        })
    }

    fun notifyException(value: VALUE? = null, exception: Throwable) {
        notifier.dispatchEvent(obtainPacket().apply {
            value?.let {
                set(value, exception)
            } ?: kotlin.run {
                this.exception = exception
            }
        })
    }

    fun notifyException(value: VALUE? = null, message: String) {
        notifyException(value, Exception(message))
    }

    companion object {

        fun <VALUE : Any?> Complete(value: VALUE) = TaskValue<VALUE>().also {
            it.notifyComplete(value)
        }.obtainScope()

        fun <VALUE : Any?> Exception(value: VALUE? = null, exception: Throwable) = TaskValue<VALUE>().also {
            it.notifyException(value, exception)
        }.obtainScope()

        fun <VALUE : Any?> Exception(value: VALUE? = null, message: String) = TaskValue<VALUE>().also {
            it.notifyException(value, message)
        }.obtainScope()

        fun <VALUE : Any?> Canceled() = TaskValue<VALUE>().also {
            it.cancel()
            it.notifyCanceled()
        }.obtainScope()

    }

}