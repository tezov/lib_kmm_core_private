

package com.tezov.lib_kmm_core.type.primitive

import com.tezov.lib_kmm_core.type.primitive.string.StringHexTo

object ByteTo {
    var BYTES = 1
    var SIZE = java.lang.Byte.SIZE

    fun Byte.toBoolean() = this == 0x01.toByte()
    fun Byte?.toBoolean() = this?.toBoolean()

    fun UByte.toBoolean() = this == 0x01.toUByte()
    fun UByte?.toBoolean() = this?.toBoolean()

    fun UByte.toStringHex() = StringHexTo.decode(this)
    fun UByte?.toStringHex() = this?.toStringHex()
}