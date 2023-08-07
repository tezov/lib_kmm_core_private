

package com.tezov.lib_kmm_core.type.primitive

import com.tezov.lib_kmm_core.type.primitive.string.StringBase49To
import com.tezov.lib_kmm_core.type.primitive.string.StringBase58To
import com.tezov.lib_kmm_core.type.primitive.string.StringBase64To
import com.tezov.lib_kmm_core.type.primitive.string.StringCharTo
import com.tezov.lib_kmm_core.type.primitive.string.StringHexCharTo
import com.tezov.lib_kmm_core.type.primitive.string.StringHexTo
import com.tezov.lib_kmm_core.util.UtilsBytes.xor

@OptIn(ExperimentalUnsignedTypes::class)
object BytesTo {

    fun uByteArrayOf(vararg elements: Int): UByteArray =
        UByteArray(elements.size) { elements[it].toUByte() }

    fun UByteArray?.toCharArray() = this?.toCharArray()
    fun UByteArray.toCharArray() = this.let {
        val c = CharArray(it.size / 2)
        for (i in it.indices step 2) {
            c[i / 2] = (it[i + 1].toInt() or (it[i].toInt() shl 8)).toChar()
        }
        c
    }

    fun UByteArray?.toLong(offset: Int = 0) = this?.toLong(offset)
    fun UByteArray.toLong(offset: Int = 0) = this.let {
        var result: Long = 0
        val end = Math.min(it.size - offset, LongTo.BYTES)
        for (i in 0 until end) {
            result = result shl ByteTo.SIZE
            result = result or it[i + offset].toLong()
        }
        result
    }

    fun UByteArray?.toDouble(offset: Int = 0) = this?.toDouble(offset)
    fun UByteArray.toDouble(offset: Int = 0) = this.let {
        assert(
            LongTo.BYTES == DoubleTo.BYTES,
            lazyMessage = { "long and double doesn't have the same size" })
        var result: Long = 0
        val end = Math.min(it.size - offset, DoubleTo.BYTES)
        for (i in 0 until end) {
            result = result shl ByteTo.SIZE
            result = result or it[i + offset].toLong()
        }
        Double.fromBits(result)
    }

    fun UByteArray?.toInt(offset: Int = 0) = this?.toInt(offset)
    fun UByteArray.toInt(offset: Int = 0) = this.let {
        var result: Int = 0
        val end = Math.min(it.size - offset, IntTo.BYTES)
        for (i in 0 until end) {
            result = result shl ByteTo.SIZE
            result = result or it[i + offset].toInt()
        }
        result
    }

    fun UByteArray?.toFloat(offset: Int = 0) = this?.toFloat(offset)
    fun UByteArray.toFloat(offset: Int = 0) = this.let {
        assert(
            IntTo.BYTES == FloatTo.BYTES,
            lazyMessage = { "int and float doesn't have the same size" })
        var result: Int = 0
        val end = Math.min(it.size - offset, FloatTo.BYTES)
        for (i in 0 until end) {
            result = result shl ByteTo.SIZE
            result = result or (it[i + offset]).toInt()
        }
        Float.fromBits(result)
    }

    fun UByteArray?.toBoolean(offset: Int = 0) = this?.toBoolean(offset)
    fun UByteArray.toBoolean(offset: Int = 0) = this[offset] == 1.toUByte()

    fun UByteArray?.toStringHex() = this?.toStringHex()
    fun UByteArray.toStringHex() = StringHexTo.decode(this)

    fun UByteArray?.toStringHexChar() = this?.toStringHexChar()
    fun UByteArray.toStringHexChar() = StringHexCharTo.decode(this)

    fun UByteArray?.toStringChar() = this?.toStringChar()
    fun UByteArray.toStringChar() = StringCharTo.decode(this)

    fun UByteArray?.toStringBase64() = this?.toStringBase64()
    fun UByteArray.toStringBase64() = StringBase64To.encode(this)

    fun UByteArray?.toStringBase58() = this?.toStringBase58()
    fun UByteArray.toStringBase58() = StringBase58To.encode(this)

    fun UByteArray?.toStringBase49() = this?.toStringBase49()
    fun UByteArray.toStringBase49() = StringBase49To.encode(this)

    fun UByteArray?.complement() = this?.complement()
    fun UByteArray.complement() = this.xor(0xFF.toUByte())

}