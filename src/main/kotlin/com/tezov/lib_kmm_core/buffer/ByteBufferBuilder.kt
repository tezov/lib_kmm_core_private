

package com.tezov.lib_kmm_core.buffer

import com.tezov.lib_kmm_core.buffer.ByteBuffer.Companion.BOOLEAN_SIZE
import com.tezov.lib_kmm_core.buffer.ByteBuffer.Companion.BYTES_SIZE
import com.tezov.lib_kmm_core.buffer.ByteBuffer.Companion.BYTE_SIZE
import com.tezov.lib_kmm_core.buffer.ByteBuffer.Companion.CHARS_SIZE
import com.tezov.lib_kmm_core.buffer.ByteBuffer.Companion.DOUBLE_SIZE
import com.tezov.lib_kmm_core.buffer.ByteBuffer.Companion.FLAG_SIZE
import com.tezov.lib_kmm_core.buffer.ByteBuffer.Companion.FLOAT_SIZE
import com.tezov.lib_kmm_core.buffer.ByteBuffer.Companion.INT_SIZE
import com.tezov.lib_kmm_core.buffer.ByteBuffer.Companion.LONG_SIZE
import com.tezov.lib_kmm_core.buffer.ByteBuffer.Companion.STRING_SIZE
import com.tezov.lib_kmm_core.type.collection.ListEntry
import com.tezov.lib_kmm_core.util.UtilsBytes
import com.tezov.lib_kmm_core.util.UtilsNull.NOT_NULL
import com.tezov.lib_kmm_core.util.UtilsNull.NULL
import kotlin.reflect.KClass
@OptIn(ExperimentalUnsignedTypes::class)
class ByteBufferBuilder private constructor() {
    private val datas: ListEntry<KClass<*>, Any?> = ListEntry()
    private var length = 0

    companion object {
        fun obtain() = ByteBufferBuilder()
    }

    private fun add(type: KClass<*>, o: Any?) {
        datas.add(type, o)
    }

    fun build(): ByteBuffer {
        val buffer = ByteBuffer.wrap(UtilsBytes.obtain(length))
        for ((type, value) in datas) {
            when (type) {
                UByteArray::class -> {
                    buffer.bytes = value as UByteArray
                }
                String::class -> {
                    buffer.string = value as String
                }
                Int::class -> {
                    buffer.int = value as Int
                }
                Long::class -> {
                    buffer.long = value as Long
                }
                Boolean::class -> {
                    buffer.boolean = value as Boolean
                }
                NULL::class -> {
                    buffer.nullFlag = false
                }
                NOT_NULL::class -> {
                    buffer.nullFlag = true
                }
                Float::class -> {
                    buffer.float = value as Float
                }
                Double::class -> {
                    buffer.double = value as Double
                }
                UByte::class -> {
                    buffer.byte = value as UByte
                }
                CharArray::class -> {
                    buffer.chars = value as CharArray
                }

                ByteBuffer::class -> {
                    value?.let {
                        when (it::class) {
                            UByteArray::class -> {
                                buffer.writeToBuffer(value as UByteArray)
                            }
                            UByte::class -> {
                                buffer.writeToBuffer(value as UByte)
                            }
                        }
                    }
                }
            }
        }
        datas.clear()
        length = 0
        return buffer
    }

    var nullFlag: Boolean
        get() = throw IllegalAccessError()
        set(flag) {
            length += FLAG_SIZE()
            add(if (flag) NOT_NULL::class else NULL::class, null)
        }

    var bytes: UByteArray?
        get() = throw IllegalAccessError()
        set(data) {
            length += BYTES_SIZE(data)
            add(UByteArray::class, data)
        }
    var byte: UByte?
        get() = throw IllegalAccessError()
        set(data) {
            length += BYTE_SIZE()
            add(UByte::class, data)
        }
    var chars: CharArray?
        get() = throw IllegalAccessError()
        set(data) {
            length += CHARS_SIZE(data)
            add(CharArray::class, data)
        }
    var string: String?
        get() = throw IllegalAccessError()
        set(data) {
            length += STRING_SIZE(data)
            add(String::class, data)
        }
    var int: Int?
        get() = throw IllegalAccessError()
        set(data) {
            length += INT_SIZE()
            add(Int::class, data)
        }
    var float: Float?
        get() = throw IllegalAccessError()
        set(data) {
            length += FLOAT_SIZE()
            add(Float::class, data)
        }
    var double: Double?
        get() = throw IllegalAccessError()
        set(data) {
            length += DOUBLE_SIZE()
            add(Double::class, data)
        }
    var long: Long?
        get() = throw IllegalAccessError()
        set(data) {
            length += LONG_SIZE()
            add(Long::class, data)
        }
    var boolean: Boolean?
        get() = throw IllegalAccessError()
        set(data) {
            length += BOOLEAN_SIZE()
            add(Boolean::class, data)
        }

    fun writeToBuffer(b: UByte) {
        length += 1
        return add(ByteBuffer::class, b)
    }

    fun writeToBuffer(bytes: UByteArray) {
        length += bytes.size
        return add(ByteBuffer::class, bytes)
    }

}