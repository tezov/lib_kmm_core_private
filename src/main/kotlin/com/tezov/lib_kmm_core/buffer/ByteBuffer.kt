

package com.tezov.lib_kmm_core.buffer

import com.tezov.lib_kmm_core.type.primitive.BooleanTo.toByte
import com.tezov.lib_kmm_core.type.primitive.ByteTo.toBoolean
import com.tezov.lib_kmm_core.type.primitive.BytesTo.toCharArray
import com.tezov.lib_kmm_core.type.primitive.BytesTo.toStringChar
import com.tezov.lib_kmm_core.type.primitive.CharsTo.toUByteArray
import com.tezov.lib_kmm_core.type.primitive.string.StringCharTo.toUByteArrayChar
import com.tezov.lib_kmm_core.type.primitive.ByteTo
import com.tezov.lib_kmm_core.type.primitive.DoubleTo
import com.tezov.lib_kmm_core.type.primitive.FloatTo
import com.tezov.lib_kmm_core.type.primitive.IntTo
import com.tezov.lib_kmm_core.type.primitive.LongTo
import com.tezov.lib_kmm_core.util.UtilsBytes
import java.nio.Buffer
import java.util.*

@OptIn(ExperimentalUnsignedTypes::class)
open class ByteBuffer protected constructor(val buffer: java.nio.ByteBuffer) {
    private constructor(bytes: UByteArray) : this(java.nio.ByteBuffer.wrap(bytes.asByteArray()))


    companion object {
        private val BYTE_LENGTH_OFFSET = IntTo.BYTES

        fun FLAG_SIZE() = ByteTo.BYTES
        fun FLAG_SIZE(length: Int) = FLAG_SIZE() * length
        fun BYTES_SIZE(length: Int) = BYTE_LENGTH_OFFSET + length
        fun BYTES_SIZE(bytes: UByteArray?) = BYTES_SIZE(bytes?.size ?: 0)

        fun BYTE_SIZE() = ByteTo.BYTES + ByteTo.BYTES
        fun BYTE_SIZE(length: Int) = BYTE_SIZE() * length
        fun CHARS_SIZE(length: Int) = BYTE_LENGTH_OFFSET + length * 2
        fun CHARS_SIZE(chars: CharArray?) = CHARS_SIZE(chars?.size ?: 0)

        fun STRING_SIZE(s: String?) = s?.let {
            val bytes: ByteArray = it.toUByteArrayChar().toByteArray()
            BYTE_LENGTH_OFFSET + bytes.size
        } ?: BYTE_LENGTH_OFFSET

        fun INT_SIZE() = ByteTo.BYTES + IntTo.BYTES
        fun INT_SIZE(length: Int) = INT_SIZE() * length
        fun LONG_SIZE() = ByteTo.BYTES + LongTo.BYTES
        fun LONG_SIZE(length: Int) = LONG_SIZE() * length
        fun FLOAT_SIZE() = ByteTo.BYTES + FloatTo.BYTES
        fun FLOAT_SIZE(length: Int) = FLOAT_SIZE() * length
        fun DOUBLE_SIZE() = ByteTo.BYTES + DoubleTo.BYTES
        fun DOUBLE_SIZE(length: Int) = DOUBLE_SIZE() * length
        fun BOOLEAN_SIZE() = ByteTo.BYTES + ByteTo.BYTES
        fun BOOLEAN_SIZE(length: Int) = BOOLEAN_SIZE() * length

        fun wrapPacked(bytes: UByteArray, offset: Int, length: Int) =
            ByteBufferPacker.unpackData(bytes)?.let {
                wrap(it).apply {
                    position = offset
                    limit = length
                }.slice
            }

        fun wrapPacked(bytes: UByteArray) = ByteBufferPacker.unpackData(bytes)?.let {
            ByteBuffer(it)
        }

        fun wrap(bytes: UByteArray, offset: Int, length: Int) = wrap(bytes).apply {
            position = offset
            limit = length
        }.slice

        fun wrap(bytes: UByteArray) = ByteBuffer(bytes)

        fun wrap(buffer: Buffer) = buffer.takeIf { it.hasArray() }?.let {
            (it.array() as? ByteArray)?.let {
                wrap(it.asUByteArray()).apply {
                    position = buffer.arrayOffset()
                    limit = buffer.capacity() + buffer.arrayOffset()
                }.slice.apply {
                    limit = buffer.limit()
                }
            }
        }

        fun wrap(byteBuffer: java.nio.ByteBuffer) = ByteBuffer(byteBuffer)
        fun obtain(length: Int) = wrap(UtilsBytes.obtain(length))
    }

    var nullFlag
        get() = buffer.get().toBoolean()
        set(flag) {
            buffer.put(flag.toByte())
        }

    val peekLength: Int
        get() {
            val position = buffer.position()
            val length = buffer.int
            buffer.position(position)
            return length
        }

    private fun putWithSize(bytes: UByteArray?) {
        bytes.takeIf { it != null && it.isNotEmpty() }?.let {
            buffer.putInt(it.size)
            buffer.put(it.asByteArray())
        } ?: let {
            buffer.putInt(0)
        }
    }

    private fun getWithSize(): UByteArray? {
        val length = buffer.int
        if (length == 0) {
            return null
        }
        if (length > buffer.capacity() - buffer.position() || length < 0) {
            return null
        } else {
            val bytes = UtilsBytes.obtain(length)
            buffer[bytes.asByteArray()]
            return bytes
        }
    }

    open var bytes
        get() = getWithSize()
        set(data) {
            putWithSize(data)
        }
    open var byte
        get() = takeIf { nullFlag == false }?.let { buffer.get().toUByte() }
        set(data) {
            data?.let {
                nullFlag = false
                buffer.put(it.toByte())
            } ?: let {
                nullFlag = true
            }
        }

    open var chars
        get() = getWithSize()?.let { it.toUByteArray().toCharArray() }
        set(data) {
            putWithSize(data?.toUByteArray())
        }

    open var string
        get() = getWithSize()?.let { it.toUByteArray().toStringChar() }
        set(data) {
            putWithSize(data?.toUByteArrayChar())
        }

    open var int
        get() = takeIf { nullFlag == false }?.let { buffer.int }
        set(data) {
            data?.let {
                nullFlag = false
                buffer.putInt(it)
            } ?: let {
                nullFlag = true
            }
        }

    open var long
        get() = takeIf { nullFlag == false }?.let { buffer.long }
        set(data) {
            data?.let {
                nullFlag = false
                buffer.putLong(it)
            } ?: let {
                nullFlag = true
            }
        }

    open var float
        get() = takeIf { nullFlag == false }?.let { buffer.float }
        set(data) {
            data?.let {
                nullFlag = false
                buffer.putFloat(it)
            } ?: let {
                nullFlag = true
            }
        }

    open var double
        get() = takeIf { nullFlag == false }?.let { buffer.double }
        set(data) {
            data?.let {
                nullFlag = false
                buffer.putDouble(it)
            } ?: let {
                nullFlag = true
            }
        }

    open var boolean
        get() = takeIf { nullFlag == false }?.let { buffer.get().toBoolean() }
        set(data) {
            data?.let {
                nullFlag = false
                buffer.put(it.toByte())
            } ?: let {
                nullFlag = true
            }
        }

    open fun writeToBuffer(b: UByte) {
        buffer.put(b.toByte())
    }

    open fun writeToBuffer(bytes: UByteArray) {
        buffer.put(bytes.asByteArray())
    }

    fun readFromBuffer(): UByte {
        return buffer.get().toUByte()
    }

    fun readFromBuffer(count: Int): UByteArray {
        val _position = position
        val bytes = Arrays.copyOfRange(byteArray, _position, _position + count)
        position = _position + count
        return bytes.asUByteArray()
    }


    val count
        get() = if (buffer.hasArray()) {
            buffer.limit() - buffer.arrayOffset()
        } else {
            buffer.limit()
        }

    val capacity get() = buffer.capacity()
    val remaining get() = buffer.remaining()
    val slice get() = wrap(buffer.slice())

    val arrayOffset get() = buffer.arrayOffset()
    var position
        get() = buffer.position()
        set(value) {
            buffer.position(value)
        }
    var limit
        get() = buffer.limit()
        set(value) {
            buffer.limit(value)
        }

    val rewind: Unit
        get() {
            buffer.rewind()
        }
    val clear: Unit
        get() {
            buffer.clear()
        }

    open val uByteArray get() = byteArray.asUByteArray()
    open val byteArray get() = buffer.array()

    val uByteArrayRemaining get() = readFromBuffer(remaining)
    val byteArrayRemaining get() = uByteArrayRemaining.asByteArray()

    val byteArrayPacked get() = uByteArrayPacked?.asByteArray()
    val uByteArrayPacked get() = ByteBufferPacker.packData(uByteArray)

}