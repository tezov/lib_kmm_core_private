

package com.tezov.lib_kmm_core.buffer

import com.tezov.lib_kmm_core.type.primitive.BooleanTo.toUByte
import com.tezov.lib_kmm_core.type.primitive.BooleanTo.toUByteArray
import com.tezov.lib_kmm_core.type.primitive.CharsTo.toUByteArray
import com.tezov.lib_kmm_core.type.primitive.DoubleTo.toUByteArray
import com.tezov.lib_kmm_core.type.primitive.FloatTo.toUByteArray
import com.tezov.lib_kmm_core.type.primitive.IntTo.toUByteArray
import com.tezov.lib_kmm_core.type.primitive.LongTo.toUByteArray
import com.tezov.lib_kmm_core.type.primitive.string.StringCharTo.toUByteArrayChar
import com.tezov.lib_kmm_core.util.UtilsStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.properties.Delegates

@OptIn(ExperimentalUnsignedTypes::class)
open class ByteBufferOutput private constructor(length: Int) : OutputStream() {
    private var bos by Delegates.notNull<ByteArrayOutputStream>()

    init {
        try {
            bos = ByteArrayOutputStream(length)
        } catch (e: Throwable) {
        }
    }

    companion object {
        const val BUFFER_INITIAL_SIZE = 1024
        private val INT0_AS_UBYTES = 0.toUByteArray()
        fun obtain(length: Int = BUFFER_INITIAL_SIZE): ByteBufferOutput {
            return ByteBufferOutput(length)
        }
    }

    private fun putWithSize(bytes: UByteArray?) {
        bytes.takeIf { it != null && it.isNotEmpty() }?.let {
            writeBytes(it.size.toUByteArray())
            writeBytes(it)
        } ?: let {
            writeBytes(INT0_AS_UBYTES)
        }
    }

    private fun writeBytes(bytes: UByteArray) {
        try {
            bos.write(bytes.asByteArray())
        } catch (e: Throwable) {
        }
    }

    private fun writeByte(bytes: UByte) {
        try {
            bos.write(byteArrayOf(bytes.toByte()))
        } catch (e: Throwable) {
        }
    }

    var nullFlag: Boolean
        get() = throw IllegalAccessError()
        set(flag) {
            writeByte(flag.toUByte())
        }

    var bytes: UByteArray?
        get() = throw IllegalAccessError()
        set(data) {
            putWithSize(data)
        }
    var byte: UByte?
        get() = throw IllegalAccessError()
        set(data) {
            data?.let {
                nullFlag = false
                writeByte(it)
            } ?: let {
                nullFlag = true
            }
        }
    var chars: CharArray?
        get() = throw IllegalAccessError()
        set(data) {
            putWithSize(data.toUByteArray())
        }
    var string: String?
        get() = throw IllegalAccessError()
        set(data) {
            putWithSize(data.toUByteArrayChar())
        }
    var int: Int?
        get() = throw IllegalAccessError()
        set(data) {
            data?.let {
                nullFlag = false
                writeBytes(data.toUByteArray())
            } ?: let {
                nullFlag = true
            }
        }
    var float: Float?
        get() = throw IllegalAccessError()
        set(data) {
            data?.let {
                nullFlag = false
                writeBytes(data.toUByteArray())
            } ?: let {
                nullFlag = true
            }
        }
    var double: Double?
        get() = throw IllegalAccessError()
        set(data) {
            data?.let {
                nullFlag = false
                writeBytes(data.toUByteArray())
            } ?: let {
                nullFlag = true
            }
        }
    var long: Long?
        get() = throw IllegalAccessError()
        set(data) {
            data?.let {
                nullFlag = false
                writeBytes(data.toUByteArray())
            } ?: let {
                nullFlag = true
            }
        }
    var boolean: Boolean?
        get() = throw IllegalAccessError()
        set(data) {
            data?.let {
                nullFlag = false
                writeBytes(data.toUByteArray())
            } ?: let {
                nullFlag = true
            }
        }

    open fun writeToBuffer(b: UByte) {
        writeByte(b)
    }

    open fun writeToBuffer(bytes: UByteArray) {
        writeBytes(bytes)
    }

    val length get() = bos.size()

    val uByteArray get() = byteArray.asUByteArray()
    val byteArray get() = bos.toByteArray()

    val byteArrayPacked get() = uByteArrayPacked?.asByteArray()
    val uByteArrayPacked get() = ByteBufferPacker.packData(uByteArray)

    val clear: Unit get() = bos.reset()

    @Throws(IOException::class)
    override fun write(b: ByteArray) {
        bos.write(b)
    }

    @Throws(IOException::class)
    override fun write(b: ByteArray, off: Int, len: Int) {
        bos.write(b, off, len)
    }

    @Throws(IOException::class)
    override fun flush() {
        bos.flush()
    }

    @Throws(IOException::class)
    override fun write(b: Int) {
        bos.write(b)
    }

    override fun close() {
        UtilsStream.closeSilently(bos)
    }

}