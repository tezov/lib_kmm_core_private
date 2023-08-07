


package com.tezov.lib_kmm_core.async.notifier.task


class TaskState : TaskValue<Nothing?>() {

    fun notifyComplete() {
        super.notifyComplete(value = null)
    }

    fun notifyException(exception: Throwable) {
        super.notifyException(value = null, exception = exception)
    }

    fun notifyException(message: String) {
        super.notifyException(value = null, message = message)
    }


    companion object {

        fun Complete() = TaskState().also {
            it.notifyComplete()
        }.obtainScope()

        fun Exception(exception: Throwable) = TaskState().also {
            it.notifyException(exception)
        }.obtainScope()

        fun Exception(message: String) = TaskState().also {
            it.notifyException(message)
        }.obtainScope()

        fun Canceled() = TaskState().also {
            it.cancel()
            it.notifyCanceled()
        }.obtainScope()

    }

}