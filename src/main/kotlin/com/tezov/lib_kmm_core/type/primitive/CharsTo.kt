

package com.tezov.lib_kmm_core.type.primitive

@OptIn(ExperimentalUnsignedTypes::class)
object CharsTo {

        fun CharArray.toUByteArray() = this.let {
        val b = UByteArray(it.size * 2)
        for (i in b.indices step 2) {
            val code = it[i / 2].code
            b[i] = (code shr 8).toUByte()
            b[i + 1] = code.toUByte()
        }
        b
    }

    fun CharArray?.toUByteArray() = this?.toUByteArray()


}