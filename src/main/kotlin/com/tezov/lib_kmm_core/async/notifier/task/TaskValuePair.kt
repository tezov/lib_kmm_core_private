


package com.tezov.lib_kmm_core.async.notifier.task

import com.tezov.lib_kmm_core.type.primaire.Pair
import com.tezov.lib_kmm_core.extension.ExtensionNull.isNotNull

class TaskValuePair<F : Any, S : Any> : TaskValue<Pair<F?, S?>?>() {

    companion object {

        fun <F : Any, S : Any> Complete(first: F?, second: S?) = TaskValuePair<F, S>().also {
            it.notifyComplete(Pair(first, second))
        }.obtainScope()

        fun <F : Any, S : Any> Exception(
            first: F? = null,
            second: S? = null,
            exception: Throwable
        ) = TaskValuePair<F, S>().also {
            if(first.isNotNull ||second.isNotNull){
                it.notifyException(Pair(first, second), exception)
            }
            else {
                it.notifyException(null, exception)
            }
        }.obtainScope()

        fun <F : Any, S : Any> Exception(first: F? = null, second: S? = null, message: String) =
            TaskValuePair<F, S>().also {
                if(first.isNotNull ||second.isNotNull){
                    it.notifyException(Pair(first, second), message)
                }
                else {
                    it.notifyException(null, message)
                }
            }.obtainScope()

        fun <F : Any, S : Any> Canceled() = TaskValuePair<F, S>().also {
            it.cancel()
            it.notifyCanceled()
        }.obtainScope()

    }


}