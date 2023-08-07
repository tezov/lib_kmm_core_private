

package com.tezov.lib_kmm_core.type.primitive.string

import com.tezov.lib_kmm_core.type.primitive.BytesTo.toStringChar
import com.tezov.lib_kmm_core.util.UtilsBytes
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalUnsignedTypes::class)
object StringBase58To {
    val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()
    private val INDEXES = IntArray(128) { -1 }.apply {
        for (i in ALPHABET.indices) {
            this[ALPHABET[i].code] = i
        }
    }

    fun encode(input: UByteArray): String {
        if (input.isEmpty()) return ""
        val _input = UtilsBytes.copy(input, 0, input.size)
        var zeroCount = 0
        while (zeroCount < _input.size && _input[zeroCount] == 0.toUByte()) {
            ++zeroCount
        }
        val temp = UByteArray(_input.size * 2)
        var j = temp.size
        var startAt = zeroCount
        while (startAt < _input.size) {
            val mod = divmod58(_input, startAt)
            if (_input[startAt] == 0x00.toUByte()) {
                ++startAt
            }
            temp[--j] = ALPHABET[mod.toInt()].code.toUByte()
        }
        while (j < temp.size && temp[j] == ALPHABET[0].code.toUByte()) {
            ++j
        }
        while (--zeroCount >= 0) {
            temp[--j] = ALPHABET[0].code.toUByte()
        }
        val output = UtilsBytes.copy(temp, j, temp.size)
        return String(output.toByteArray(), StandardCharsets.UTF_8)
    }

    fun decode(input: String): UByteArray {
        val input58 = UByteArray(input.length)
        for (i in input.indices) {
            val c = input[i]
            var digit58 = -1
            if (c.code < 128) {
                digit58 = INDEXES[c.code]
            }
            if (digit58 < 0) {
                return UByteArray(0)
            }
            input58[i] = digit58.toUByte()
        }
        var zeroCount = 0
        while (zeroCount < input58.size && input58[zeroCount] == 0.toUByte()) {
            ++zeroCount
        }
        val temp = UByteArray(input.length)
        var j = temp.size
        var startAt = zeroCount
        while (startAt < input58.size) {
            val mod = divmod256(input58, startAt)
            if (input58[startAt] == 0.toUByte()) {
                ++startAt
            }
            temp[--j] = mod
        }
        while (j < temp.size && temp[j] == 0.toUByte()) {
            ++j
        }
        return UtilsBytes.copy(temp, j - zeroCount, temp.size)
    }

    private fun divmod58(number: UByteArray, startAt: Int): UByte {
        var remainder = 0
        for (i in startAt until number.size) {
            val digit256 = number[i].toInt() and 0xFF
            val temp = remainder * 256 + digit256
            number[i] = (temp / 58).toUByte()
            remainder = temp % 58
        }
        return remainder.toUByte()
    }

    private fun divmod256(number58: UByteArray, startAt: Int): UByte {
        var remainder = 0
        for (i in startAt until number58.size) {
            val digit58 = number58[i].toInt() and 0xFF
            val temp = remainder * 58 + digit58
            number58[i] = (temp / 256).toUByte()
            remainder = temp % 256
        }
        return remainder.toUByte()
    }

    fun String.toUByteArrayBase58() = decode(this)
    fun String?.toUByteArrayBase58() = this?.toUByteArrayBase58()

    fun String?.toStringChar() = this?.toUByteArrayBase58()?.toStringChar()

}