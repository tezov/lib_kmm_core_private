package com.tezov.lib_kmm_core.async.notifier

import com.tezov.lib_kmm_core.extension.ExtensionFlow.collectForever
import com.tezov.lib_kmm_core.extension.ExtensionFlow.collectOnce
import com.tezov.lib_kmm_core.extension.ExtensionFlow.collectUntil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

object Notifier {

    class Emitter<T:Any>(
        replay: Int = 0,
        extraBufferCapacity: Int = 1,
        onBufferOverflow: BufferOverflow = BufferOverflow.DROP_OLDEST
    ) {
        internal val flow = MutableSharedFlow<T>(
            replay = replay,
            extraBufferCapacity = extraBufferCapacity,
            onBufferOverflow = onBufferOverflow
        )

        fun tryEmit(event: T) = flow.tryEmit(event)

        suspend fun emit(event: T) = flow.emit(event)

        val createCollector get() = Collector(this)
    }

    class Collector<T:Any>(
        private val emitter:Emitter<T>
    ) {

        fun once(scope: CoroutineScope, block: suspend (T) -> Unit) = emitter.flow.collectOnce(scope, block)

        fun forever(scope: CoroutineScope, block: suspend (T) -> Unit) = emitter.flow.collectForever(scope, block)

        fun until(scope: CoroutineScope, block: suspend (T) -> Boolean) = emitter.flow.collectUntil(scope, block)

    }

}