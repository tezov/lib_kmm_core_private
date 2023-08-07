

package com.tezov.lib_kmm_core.type.primitive.string

import android.util.Base64
import com.tezov.lib_kmm_core.type.primitive.BytesTo.toStringChar

@OptIn(ExperimentalUnsignedTypes::class)
object StringBase64To {
    fun encode(b: UByteArray) = Base64.encodeToString(b.toByteArray(), Base64.DEFAULT)
    fun decode(s: String) = Base64.decode(s, Base64.DEFAULT).toUByteArray()

    fun String.toUByteArrayBase64() = decode(this)
    fun String?.toUByteArrayBase64() = this?.toUByteArrayBase64()

    fun String?.toStringChar() = this?.toUByteArrayBase64()?.toStringChar()
}