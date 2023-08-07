

package com.tezov.lib_kmm_core.type.primitive

import com.tezov.lib_kmm_core.type.primitive.BytesTo.toStringHex
import com.tezov.lib_kmm_core.type.primitive.string.StringHexTo

@OptIn(ExperimentalUnsignedTypes::class)
object IntTo {
    var BYTES = Integer.SIZE / ByteTo.SIZE

    var MAX_DIGIT_POSITIVE = Int.MAX_VALUE.toString().length
    var MAX_DIGIT_NEGATIVE = Int.MIN_VALUE.toString().length - 1

    fun Int.toUByteArray() = ubyteArrayOf(
        (this shr 24).toUByte(),
        (this shr 16).toUByte(),
        (this shr 8).toUByte(),
        this.toUByte()
    )

    fun Int?.toUByteArray() = this?.toUByteArray()

    fun Int.toStringHex(addPrefix: Boolean = false) =
        if (!addPrefix) this.toUByteArray().toStringHex()
        else StringHexTo.HEX_PREFIX + this.toUByteArray().toStringHex()

    fun Int?.toStringHex(addPrefix: Boolean = false) = this?.toStringHex(addPrefix)

}