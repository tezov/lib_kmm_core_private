

package com.tezov.lib_kmm_core.type.primitive.string

import com.tezov.lib_kmm_core.type.primitive.BytesTo.complement
import com.tezov.lib_kmm_core.type.primitive.BytesTo.toStringChar
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalUnsignedTypes::class)
object StringHexCharTo {
    val HEX_CHAR_ARRAY = "ABCDEFGHIJKLMNOP".toByteArray(StandardCharsets.UTF_8).asUByteArray()
    const val HEX_PREFIX = "0x"
    private fun digit(c: Char): Int {
        return if (c in 'A'..'P') {
            c - 'A'
        } else {
            -1
        }
    }

    fun encode(s: String): UByteArray {
        var _s = s
        if (_s.startsWith(HEX_PREFIX)) {
            _s = _s.substring(2)
        }
        val len = _s.length
        val parity = len % 2
        val data = UByteArray(len / 2 + parity)
        val end = len - parity
        var i = 0
        while (i < end) {
            data[i ushr 1] = (digit(_s[i + 1]) + (digit(_s[i]) shl 4)).toUByte()
            i += 2
        }
        if (end < len) {
            data[end ushr 2] = (digit(_s[end]) shl 4).toUByte()
        }
        return data
    }

    fun decode(b: UByteArray): String {
        val hexChars = UByteArray(b.size * 2)
        for (j in b.indices) {
            val v: Int = (b[j] and 0xFFU).toInt()
            hexChars[j * 2] = HEX_CHAR_ARRAY[v ushr 4]
            hexChars[j * 2 + 1] = HEX_CHAR_ARRAY[v and 0x0F]
        }
        return String(hexChars.toByteArray(), StandardCharsets.ISO_8859_1)
    }

    fun decode(b: UByte): String {
        val hexChars = UByteArray(2)
        val v: Int = (b and 0xFFU).toInt()
        hexChars[0] = HEX_CHAR_ARRAY[v ushr 4]
        hexChars[1] = HEX_CHAR_ARRAY[v and 0x0F]
        return String(hexChars.toByteArray(), StandardCharsets.ISO_8859_1)
    }

    fun String.toUByteArrayHexChar() = encode(this)
    fun String?.toUByteArrayHexChar() = this?.toUByteArrayHexChar()

//    fun String?.toInt() = this?.toUByteArrayHexChar()?.toInt()
//    fun String?.toLong() = this?.toUByteArrayHexChar()?.toLong()
//    fun String?.toDouble() = this?.toUByteArrayHexChar()?.toDouble()
//    fun String?.toFloat() = this?.toUByteArrayHexChar()?.toFloat()
//    fun String?.toBoolean() = this?.toUByteArrayHexChar()?.toBoolean()

    fun String?.complement() = this?.toUByteArrayHexChar()?.complement()?.toStringChar()

}