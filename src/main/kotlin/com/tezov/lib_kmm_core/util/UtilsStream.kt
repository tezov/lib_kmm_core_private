

package com.tezov.lib_kmm_core.util

import java.io.*

object UtilsStream {

    const val NULL_LENGTH = -1

    fun closeSilently(input: InputStream?) {
        input?.let {
            try {
                it.close()
            } catch (_: Throwable) {
            }
        }
    }

    fun closeSilently(output: OutputStream?) {
        output?.let {
            try {
                it.close()
            } catch (_: Throwable) {
            }
        }
    }

}