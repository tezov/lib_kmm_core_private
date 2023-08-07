

package com.tezov.lib_kmm_core.type.primitive

import com.tezov.lib_kmm_core.type.primitive.BytesTo.toStringHex

@OptIn(ExperimentalUnsignedTypes::class)
object LongTo {
    var BYTES = java.lang.Long.SIZE / ByteTo.SIZE

    fun Long.toUByteArray() = ubyteArrayOf(
        (this shr 56).toUByte(),
        (this shr 48).toUByte(),
        (this shr 40).toUByte(),
        (this shr 32).toUByte(),
        (this shr 24).toUByte(),
        (this shr 16).toUByte(),
        (this shr 8).toUByte(),
        this.toUByte()
    )

    fun Long?.toUByteArray() = this?.toUByteArray()

    fun Long.toStringHex() = this.toUByteArray().toStringHex()

    fun Long?.toStringHex() = this?.toStringHex()

}