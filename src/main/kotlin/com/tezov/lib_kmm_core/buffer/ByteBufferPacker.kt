

package com.tezov.lib_kmm_core.buffer

import com.tezov.lib_kmm_core.buffer.ByteBufferOutput.Companion.BUFFER_INITIAL_SIZE
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalUnsignedTypes::class)
class ByteBufferPacker {
    private var output: NibbleOutputStream? = null
    private var input: NibbleInputStreamStack? = null
    private var bufferOut: NibbleOutputStream? = null

    open class NibbleInputStream(
        data: UByteArray,
        offset: Int = 0,
        length: Int = data.size,
        isOdd: Boolean
    ) {
        private val input = ByteArrayInputStream(data.asByteArray(), offset, length)
        private var isTop = false
        private var bLow: UByte = 0x00.toUByte()
        private var bHigh: UByte = 0x00.toUByte()
        private var length: Int

        init {
            this.length = length * 2
            if (isOdd) {
                this.length -= 1
            }
        }

        val isOdd: Boolean
            get() = length % 2 != 0
        val isEven: Boolean
            get() = length % 2 == 0

        open fun available() = length

        open fun read(): UByte {
            if (length <= 0) {
                return END_STREAM
            }
            isTop = !isTop
            length -= 1
            return if (isTop) {
                val b = input.read()
                bHigh = (b shr 4).toUByte()
                bLow = (b and 0x0F).toUByte()
                bHigh
            } else {
                bLow
            }
        }
    }

    open class NibbleInputStreamStack(
        data: UByteArray,
        offset: Int = 0,
        length: Int = data.size,
        isOdd: Boolean
    ) : NibbleInputStream(data, offset, length, isOdd) {
        private val tmpOut: NibbleOutputStream = NibbleOutputStream()
        private var tmpIn: NibbleInputStream? = null
        private var length: Int = super.available()

        override fun available() = length

        private fun updateTmpIn() {
            tmpIn.let {
                if (it == null || it.available() <= 0) {
                    if (tmpOut.length() > 0) {
                        tmpIn = NibbleInputStream(tmpOut.toBytes(), isOdd = tmpOut.isOdd)
                        tmpOut.clear()
                    } else {
                        tmpIn = null
                    }
                }
            }
        }

        override fun read(): UByte {
            if (length <= 0) {
                return END_STREAM
            }
            updateTmpIn()
            length -= 1
            return tmpIn?.read() ?: super.read()
        }

        fun write(b: UByte) {
            tmpOut.write(b)
            length += 1
        }
    }

    class NibbleOutputStream {
        private val out: ByteArrayOutputStream = ByteArrayOutputStream(BUFFER_INITIAL_SIZE)
        private var isHigh = false
        private var b: Int = 0
        private var length = 0
        fun length() = length

        init {
            clear()
        }

        val isOdd: Boolean
            get() = length % 2 != 0
        val isEven: Boolean
            get() = length % 2 == 0

        fun write(n: UByte) {
            if (isHigh) {
                b = (n.toInt() shl 4)
            } else {
                b = b or (n.toInt() and 0x0F)
                write()
            }
            isHigh = !isHigh
            length++
        }

        private fun write() {
            try {
                out.write(b)
            } catch (e: Throwable) {
            }
        }

        fun toBytes(): UByteArray {
            if (!isHigh) {
                b = (b and 0xF0)
                write()
            }
            return out.toByteArray().asUByteArray()
        }

        fun clear() {
            out.reset()
            length = 0
            isHigh = true
            b = 0x00
        }
    }

    fun pack(data: UByteArray, offset: Int = 0, length: Int = data.size): UByteArray? {
        var dataOut: UByteArray? = null
        try {
            output = NibbleOutputStream()
            input = NibbleInputStreamStack(data, offset, length, false)
            bufferOut = NibbleOutputStream()
            while (input!!.available() > 0) {
                val nibble = input!!.read()
                if (nibble == ODD_VALUE) {
                    val count = packCountOdd()
                    output!!.write(ODD_CODE)
                    output!!.write(count.toUByte())
                } else {
                    bufferOut!!.clear()
                    bufferOut!!.write(nibble)
                    val count = packCountEven()
                    val isOdd = count % 2 == 1
                    val chain = (count / (EVEN_MAX_CHAIN + 1) + 1).toUByte()
                    val code = ((if (isOdd) EVEN_CODE_MASK_ODD else 0x00.toUByte()) or chain)
                    val lengthNibble = (count % (EVEN_MAX_CHAIN + 1) / 2).toUByte()
                    output!!.write(code)
                    output!!.write(lengthNibble)
                    val bufferIn =
                        NibbleInputStream(bufferOut!!.toBytes(), isOdd = bufferOut!!.isOdd)
                    var bufferNibble: UByte
                    while (bufferIn.read().also { bufferNibble = it } != END_STREAM) {
                        output!!.write(bufferNibble)
                    }
                }
            }
            dataOut = output!!.toBytes()
        } catch (e: Throwable) {
        }
        output = null
        input = null
        bufferOut = null
        return dataOut
    }

    private fun packCountOdd(): Int {
        var count = 0
        var nibble: UByte = 0x00.toUByte()
        while (count < ODD_MAX_NIBBLE && (input!!.read().also { nibble = it }) != END_STREAM) {
            if (nibble == ODD_VALUE) {
                count++
            } else {
                input!!.write(nibble)
                break
            }
        }
        return count
    }

    private fun packCountEven(): Int {
        var count = 0
        var countOdd = 0
        while (count < EVEN_MAX_NIBBLE) {
            val nibble = input!!.read()
            if (nibble != END_STREAM) {
                if (nibble != ODD_VALUE) {
                    if (countOdd != 0) {
                        val diff = (count + countOdd).toInt() - EVEN_MAX_NIBBLE
                        if (diff > 0) {
                            countOdd -= diff
                            for (i in 0 until countOdd) {
                                packCountEven_WriteBuffer(ODD_VALUE)
                            }
                            count += countOdd
                            for (i in 0 until diff) {
                                input!!.write(ODD_VALUE)
                            }
                            input!!.write(nibble)
                            break
                        } else {
                            for (i in 0 until countOdd) {
                                packCountEven_WriteBuffer(ODD_VALUE)
                            }
                            count += countOdd
                            countOdd = 0
                            packCountEven_WriteBuffer(nibble)
                            count++
                        }
                    } else {
                        packCountEven_WriteBuffer(nibble)
                        count++
                    }
                } else if (countOdd < EVEN_FOLLOWING_ODD_ALLOWED) {
                    countOdd++
                } else {
                    for (i in 0..countOdd) {
                        input!!.write(ODD_VALUE)
                    }
                    break
                }
            } else {
                for (i in 0 until countOdd) {
                    input!!.write(ODD_VALUE)
                }
                break
            }
        }
        return count
    }

    private fun packCountEven_WriteBuffer(nibble: UByte) {
        if (bufferOut != null) {
            bufferOut!!.write(nibble)
        }
    }

    fun unpack(data: UByteArray, offset: Int = 0, length: Int = data.size): UByteArray? {
        var dataOut: UByteArray? = null
        try {
            output = NibbleOutputStream()
            input = NibbleInputStreamStack(data, offset, length, false)
            while (input!!.available() > 0) {
                val code = input!!.read()
                val lengthNibble = input!!.read()
                if (lengthNibble == END_STREAM) {
                    break
                }
                if (code == ODD_CODE) {
                    val count = lengthNibble.toInt() + 1
                    for (i in 0 until count) {
                        output!!.write(ODD_VALUE)
                    }
                } else {
                    val isOdd = (code and EVEN_CODE_MASK_ODD) == EVEN_CODE_MASK_ODD
                    val chain = (code and EVEN_CODE_MASK_ODD.inv()).toInt()
                    var count = 1 + (chain - 1) * (EVEN_MAX_CHAIN + 1) + lengthNibble.toInt() * 2
                    if (isOdd) {
                        count += 1
                    }
                    for (i in 0 until count) {
                        output!!.write(input!!.read())
                    }
                }
            }
            dataOut = output!!.toBytes()
        } catch (e: Throwable) {
        }
        output = null
        input = null
        return dataOut
    }

    companion object {
        private val packer = ByteBufferPacker()

        fun packData(data: UByteArray): UByteArray? {
            synchronized(this) {
                return packer.pack(data)
            }
        }

        fun unpackData(data: UByteArray): UByteArray? {
            synchronized(this) {
                return packer.unpack(data)
            }
        }

        private val END_STREAM: UByte = 0x10.toUByte()
        private val ODD_CODE: UByte = 0x00.toUByte()
        private val ODD_VALUE: UByte = 0x00.toUByte()
        private val ODD_MAX_NIBBLE = 0x0F
        private val EVEN_MAX_NIBBLE = (0x1E * 7)
        private val EVEN_MAX_CHAIN = 0x1F
        private val EVEN_FOLLOWING_ODD_ALLOWED = 4
        private val EVEN_CODE_MASK_ODD = 0x08.toUByte()
    }
}