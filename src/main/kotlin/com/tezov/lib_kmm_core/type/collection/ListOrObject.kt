



package com.tezov.lib_kmm_core.type.collection

import androidx.annotation.RequiresApi
import com.tezov.lib_kmm_core.extension.ExtensionList.NULL_INDEX
import com.tezov.lib_kmm_core.util.UtilsNull.NULL
import java.util.function.Consumer

class ListOrObject<T>(
    private var value: Any? = NULL,
    private var isList: Boolean = false,
    private val supplier: () -> MutableList<T> = { ArrayList() }
) : MutableList<T> {

    override fun isEmpty() = value === NULL

    override val size
        get() = when {
            value === NULL -> 0
            isList -> (value as List<*>).size
            else -> 1
        }

    override fun indexOf(element: T) = indexOf(element, 0)

    fun indexOf(element: T, offset: Int = 0): Int {
        //TODO improve if NULL_OBJECT / value or list and drop
        iterator().also {
            var i = 0
            while (it.hasNext()) {
                if(i < offset) continue
                if (it.next() == element) return i
                i++
            }
        }
        return NULL_INDEX
    }

    fun indexOf(offset: Int = 0, predicate: (T) -> Boolean): Int {
        var i = 0
        val iterator: Iterator<T> = iterator()
        while (iterator.hasNext()) {
            if(i < offset) continue
            if (predicate(iterator.next())) {
                return i
            }
            i++
        }
        return NULL_INDEX
    }

    override fun lastIndexOf(element: T) =  throw UnsupportedOperationException()

    @Suppress("UNCHECKED_CAST")
    fun get() = when {
        value === NULL -> null
        !isList -> value as? T
        else -> (value as List<*>)[0] as T
    }

    fun set(t: T?) {
        if (isList) throw UnsupportedOperationException()
        value = t ?: NULL
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(index: Int) = when {
        isList -> (value as List<T>)[index]
        value !== NULL && index == 0 ->  value as T
        else -> throw IndexOutOfBoundsException()
    }

    @Suppress("UNCHECKED_CAST")
    override fun add(element: T) = when {
        value === NULL -> {
            value = element
            true
        }
        isList -> (value as MutableList<T>).add(element)
        else -> {
            val list: MutableList<T> = supplier()
            var result = true
            result = result and list.add(value as T)
            result = result and list.add(element)
            value = list
            isList = true
            result
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun set(index: Int, element: T): T {
        if (element == null) throw UnsupportedOperationException()
        return when {
            isList -> (value as MutableList<T>).set(index, element)
            value !== NULL && index == 0 -> {
                val old = value as T
                value = element
                old
            }
            else -> throw IndexOutOfBoundsException()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun add(index: Int, element: T) {
        if (element == null) {
            throw UnsupportedOperationException()
        }
        when {
            isList -> (value as MutableList<T>).add(index, element)
            value !== NULL -> {
                val list: MutableList<T> = supplier()
                if (index == 1) {
                    list.add(value as T)
                    list.add(element)
                } else if (index == 0) {
                    list.add(element)
                    list.add(value as T)
                }
                value = list
                isList = true
            }
            index == 0 -> value = element
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun remove(element: T): Boolean {
        return when {
            value === NULL -> false
            isList -> {
                if (!(value as MutableList<*>).remove(element)) {
                    return false
                }
                if ((value as List<*>).size == 1) {
                    value = (value as List<*>)[0]
                    isList = false
                }
                true
            }
            value == element -> {
                value = NULL
                true
            }
            else -> false
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeAt(index: Int): T {
        if (isList) {
            val t = (value as MutableList<T>).removeAt(index)
            if ((value as List<*>).size == 1) {
                value = (value as List<*>)[0]
                isList = false
            }
            return t
        }
        if (index == 0) {
            val t = value as T
            value = NULL
            return t
        }
        throw IndexOutOfBoundsException()
    }

    override fun clear() {
        value = NULL
        isList = false
    }

    override operator fun contains(element: T): Boolean {
        for (t in this) {
            if (t == element) return true
        }
        return false
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return if (isList) {
            (value as List<*>).containsAll(elements)
        } else {
            for (p in elements) {
                if (p != value) return false
            }
            value === NULL || elements.size == 1
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun addAll(elements: Collection<T>): Boolean {
        return if (isList) {
            (value as MutableList<T>).addAll(elements)
        } else {
            val previousSize = size
            for (t in elements) add(t)
            previousSize + elements.size == size
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return if (isList) {
            (value as MutableList<T>).addAll(index, elements)
        } else {
            val previousSize = size
            var _index = index
            for (t in elements) add(_index++, t)
            previousSize + elements.size == size
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun retainAll(elements: Collection<T>): Boolean {
        return if (isList) {
            (value as MutableList<T>).retainAll(elements)
        } else {
            for (p in elements) {
                if (p == value) return false
            }
            clear()
            true
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeAll(elements: Collection<T>): Boolean {
        return if (isList) {
            (value as MutableList<T>).removeAll(elements)
        } else {
            for (p in elements) {
                if (p == value) {
                    clear()
                    return true
                }
            }
            false
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T>
        = throw UnsupportedOperationException()

    open inner class Itr internal constructor(internal var index:Int = 0) : MutableIterator<T> {
        internal var limit = this@ListOrObject.size
        internal var lastRet = -1
        override fun hasNext() = index < limit

        override fun next(): T {
            val i = index
            if (i >= limit) throw NoSuchElementException()
            if (i >= this@ListOrObject.size) throw ConcurrentModificationException()
            index++
            return this@ListOrObject[i.also { lastRet = it }]
        }

        override fun remove() {
            if (lastRet < 0) throw IllegalStateException()
            try {
                this@ListOrObject.removeAt(lastRet)
                index = lastRet
                lastRet = -1
                limit--
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }

        @RequiresApi(api = 24)
        override fun forEachRemaining(consumer: Consumer<in T>) {
            val size = this@ListOrObject.size
            var i = index
            if (i >= size) return
            while (i != size) {
                consumer.accept(this@ListOrObject[i++])
            }
            index = i
            lastRet = i - 1
        }
    }

    inner class ListItr internal constructor(index: Int) : Itr(index), MutableListIterator<T> {

        override fun hasPrevious() = index != 0

        override fun nextIndex() = index

        override fun previousIndex() = index - 1

        override fun previous(): T {
            val i: Int = index - 1
            if (i < 0) throw NoSuchElementException()
            if (i >= this@ListOrObject.size) throw ConcurrentModificationException()
            index = i
            return this@ListOrObject[i.also { lastRet = it }]
        }

        override fun set(element: T) {
            if (lastRet < 0) throw IllegalStateException()
            try {
                this@ListOrObject[lastRet] = element
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }

        override fun add(element: T) {
            try {
                val i: Int = index
                this@ListOrObject.add(i, element)
                index = i + 1
                lastRet = -1
                limit++
            } catch (ex: IndexOutOfBoundsException) {
                throw ConcurrentModificationException()
            }
        }
    }

    override fun iterator() = Itr()

    override fun listIterator() = ListItr(0)

    override fun listIterator(index: Int) = ListItr(index)

    companion object {

        inline val <T:Any> T?.asListOrObject: ListOrObject<T>?
            get() = this?.let {
                ListOrObject<T>(it, it is List<*>)
            }

    }
}