

package com.tezov.lib_kmm_core.buffer

import com.tezov.lib_kmm_core.type.primitive.BytesTo.toBoolean
import com.tezov.lib_kmm_core.type.primitive.BytesTo.toCharArray
import com.tezov.lib_kmm_core.type.primitive.BytesTo.toDouble
import com.tezov.lib_kmm_core.type.primitive.BytesTo.toFloat
import com.tezov.lib_kmm_core.type.primitive.BytesTo.toInt
import com.tezov.lib_kmm_core.type.primitive.BytesTo.toLong
import com.tezov.lib_kmm_core.type.primitive.BytesTo.toStringChar
import com.tezov.lib_kmm_core.type.primitive.ByteTo
import com.tezov.lib_kmm_core.type.primitive.DoubleTo
import com.tezov.lib_kmm_core.type.primitive.FloatTo
import com.tezov.lib_kmm_core.type.primitive.IntTo
import com.tezov.lib_kmm_core.type.primitive.LongTo
import com.tezov.lib_kmm_core.util.UtilsBytes
import com.tezov.lib_kmm_core.util.UtilsStream
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import kotlin.properties.Delegates

@OptIn(ExperimentalUnsignedTypes::class)
open class ByteBufferInput private constructor(bytes: UByteArray) : InputStream() {
    private var bis by Delegates.notNull<ByteArrayInputStream>()

    init {
        try {
            bis = ByteArrayInputStream(bytes.asByteArray())
        } catch (e: Throwable) {
        }
    }

    companion object {
        fun wrap(bytes: UByteArray): ByteBufferInput = ByteBufferInput(bytes)
        fun wrapPacked(bytes: UByteArray) = ByteBufferPacker.unpackData(bytes)?.let {
            ByteBufferInput(it)
        }
    }


    private fun getWithSize(): UByteArray = readBytes(IntTo.BYTES).toInt().let { readBytes(it) }
    private fun readBytes(length: Int): UByteArray {
        val bytes = UtilsBytes.obtain(length)
        bis.read(bytes.asByteArray())
        return bytes
    }

    private fun readByte() = bis.read().toUByte()

    val nullFlag get() = readBytes(1).toBoolean()


    val bytes get() = getWithSize()
    val byte get() = takeIf { nullFlag == false }?.let { readByte() }

    val chars get() = getWithSize()?.let { it.toUByteArray().toCharArray() }
    val string get() = getWithSize()?.let { it.toUByteArray().toStringChar() }
    val int get() = takeIf { nullFlag == false }?.let { readBytes(IntTo.BYTES).toInt() }
    val long get() = takeIf { nullFlag == false }?.let { readBytes(LongTo.BYTES).toLong() }
    val float get() = takeIf { nullFlag == false }?.let { readBytes(FloatTo.BYTES).toFloat() }
    val double get() = takeIf { nullFlag == false }?.let { readBytes(DoubleTo.BYTES).toDouble() }
    val boolean get() = takeIf { nullFlag == false }?.let { readBytes(ByteTo.BYTES).toBoolean() }

    fun readFromBuffer(): UByte {
        return readByte()
    }

    fun readFromBuffer(count: Int): UByteArray {
        return readBytes(count)
    }


    val remaining get() = bis.available()

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        return bis.read(b)
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return bis.read(b, off, len)
    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        return bis.skip(n)
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return bis.available()
    }

    @Synchronized
    override fun mark(readlimit: Int) {
        bis.mark(readlimit)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        bis.reset()
    }

    override fun markSupported(): Boolean {
        return bis.markSupported()
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return bis.read()
    }

    override fun close() {
        UtilsStream.closeSilently(bis)
    }


}