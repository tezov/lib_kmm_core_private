

package com.tezov.lib_kmm_core.extension

import java.util.*

@OptIn(ExperimentalUnsignedTypes::class)
object ExtensionNull {

    inline val Any?.isNull get() = this == null

    inline val Any?.isNotNull get() = this != null

    fun <T> MutableList<T>?.nullify() = if (this != null && this.isNotEmpty()) this else null
    fun <T> Collection<T>?.nullify() = if (this != null && this.isNotEmpty()) this else null

    fun String?.nullify() = if (this != null && this.isNotEmpty()) this else null
    fun String?.isNullOrEmpty() = this?.let { isEmpty() } ?: let { true }
    fun String?.isNotNullAndNotEmpty() = this?.let { isNotEmpty() } ?: let { false }

    fun CharSequence?.isNullOrEmpty() = this?.let { isEmpty() } ?: let { true }
    fun CharSequence?.isNotNullAndNotEmpty() = this?.let { isNotEmpty() } ?: let { false }

    fun ByteArray?.nullify() = this?.let {
        val byte = 0.toByte()
        Arrays.fill(this, byte)
    }

    fun ByteArray?.isNullOrEmpty() = this?.let { isEmpty() } ?: let { true }
    fun ByteArray?.isNotNullAndNotEmpty() = this?.let { isNotEmpty() } ?: let { false }

    fun UByteArray?.nullify() = this?.let {
        val byte = 0.toByte()
        Arrays.fill(this.asByteArray(), byte)
    }

    fun UByteArray?.isNullOrEmpty() = this?.let { isEmpty() } ?: let { true }
    fun UByteArray?.isNotNullAndNotEmpty() = this?.let { isNotEmpty() } ?: let { false }

    fun CharArray?.nullify() = this?.let {
        val char = 0.toChar()
        Arrays.fill(this, char)
    }
    fun CharArray?.isNullOrEmpty() = this?.let { isEmpty() } ?: let { true }
    fun CharArray?.isNotNullAndNotEmpty() = this?.let { isNotEmpty() } ?: let { false }

    inline val Any?.simpleName get() = this?.let { this::class.simpleName }

}
