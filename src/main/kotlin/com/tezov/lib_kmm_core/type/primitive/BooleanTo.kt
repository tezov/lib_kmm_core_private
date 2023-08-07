

package com.tezov.lib_kmm_core.type.primitive

@OptIn(ExperimentalUnsignedTypes::class)
object BooleanTo {

    fun Boolean.toByte() = if (this) 1.toByte() else 0.toByte()
    fun Boolean?.toByte() = this?.toByte()

    fun Boolean.toUByte() = if (this) 1.toUByte() else 0.toUByte()
    fun Boolean?.toUByte() = this?.toUByte()

    fun Boolean.toUByteArray() = ubyteArrayOf(this.toUByte())
    fun Boolean?.toUByteArray() = this?.toUByteArray()
}