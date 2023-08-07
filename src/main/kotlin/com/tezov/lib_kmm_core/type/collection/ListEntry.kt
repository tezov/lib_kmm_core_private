

package com.tezov.lib_kmm_core.type.collection

import com.tezov.lib_kmm_core.extension.ExtensionIterable.find
import com.tezov.lib_kmm_core.extension.ExtensionIterable.findIndex
import com.tezov.lib_kmm_core.extension.ExtensionIterable.forEach
import com.tezov.lib_kmm_core.type.primaire.Entry
import com.tezov.lib_kmm_core.extension.ExtensionList.NULL_INDEX
import com.tezov.lib_kmm_core.extension.ExtensionList.isNotNullIndex

class ListEntry<KEY, VALUE>(supplier: () -> MutableList<Entry<KEY, VALUE>> = { ArrayList() }) :
    MutableList<Entry<KEY, VALUE>> {

    private val list = supplier()

    fun hasKey(key: KEY, offset: Int = 0) = getEntry(key, offset) != null
    fun isUnique(key: KEY, offset: Int = 0): Boolean {
        var found = false
        list.forEach(offset) {
            if (it.key == key) {
                if (found) return false
                found = true
            }
        }
        return found
    }

    override fun isEmpty() = list.isEmpty()

    override val size get() = list.size

    override fun indexOf(element: Entry<KEY, VALUE>) = list.indexOf(element)

    fun indexOf(element: Entry<KEY, VALUE>, offset: Int = 0) = list.findIndex(offset) {
        it == element
    } ?: NULL_INDEX

    fun indexOfValue(value: VALUE, offset: Int = 0) = list.findIndex(offset) {
        it.value == value
    } ?: NULL_INDEX

    fun indexOfKey(key: KEY, offset: Int = 0) = list.findIndex(offset) {
        it.key == key
    } ?: NULL_INDEX

    fun indexOf(offset: Int = 0, predicate: (Entry<KEY, VALUE>) -> Boolean) =
        list.findIndex(offset, predicate) ?: NULL_INDEX

    override fun get(index: Int) = list[index]

    override fun lastIndexOf(element: Entry<KEY, VALUE>) = throw UnsupportedOperationException()

    fun getKey(value: VALUE, offset: Int = 0) = list.find(offset) { it.value == value }?.key
    fun getKeyAt(index: Int): KEY = list[index].key
    val keys: List<KEY>
        get() = java.util.ArrayList<KEY>(list.size).also { listKeys ->
            iteratorKeys().forEach { listKeys.add(it) }
        }

    fun getValue(key: KEY, offset: Int = 0) = list.find(offset) { it.key == key }?.value
    fun getValueAt(index: Int) = list[index].value
    val values: List<VALUE>
        get() = java.util.ArrayList<VALUE>(list.size).also { listValues ->
            iteratorValues().forEach { listValues.add(it) }
        }

    fun getEntry(key: KEY, offset: Int = 0) =
        list.find(offset) { it.key == key }

    fun getEntry(index: Int) = list[index]

    override fun add(element: Entry<KEY, VALUE>) = list.add(element)

    fun add(key: KEY, value: VALUE) = list.add(Entry(key, value))

    fun put(key: KEY, value: VALUE) = getEntry(key)?.let {
        it.value = value
        false
    } ?: run {
        list.add(Entry(key, value))
        true
    }

    fun set(key: KEY, value: VALUE) = getEntry(key)?.let {
        it.value = value
        true
    } ?: run {
        false
    }

    override fun set(index: Int, element: Entry<KEY, VALUE>): Entry<KEY, VALUE> =
        throw UnsupportedOperationException()

    override fun add(index: Int, element: Entry<KEY, VALUE>) =
        throw UnsupportedOperationException()

    override fun removeAt(index: Int) = list.removeAt(index)

    fun removeKey(key: KEY, offset: Int = 0) =
        indexOfKey(key, offset).takeIf { it.isNotNullIndex }?.let {
            list.removeAt(it).value
        }

    fun removeValue(value: VALUE, offset: Int = 0) =
        indexOfValue(value, offset).takeIf { it.isNotNullIndex }?.let {
            list.removeAt(it).key
        }

    override fun remove(element: Entry<KEY, VALUE>) =
        throw UnsupportedOperationException()

    override fun clear() = list.clear()

    override fun contains(element: Entry<KEY, VALUE>) =
        throw UnsupportedOperationException()

    override fun containsAll(elements: Collection<Entry<KEY, VALUE>>) =
        throw UnsupportedOperationException()

    override fun retainAll(elements: Collection<Entry<KEY, VALUE>>) =
        throw UnsupportedOperationException()

    override fun removeAll(elements: Collection<Entry<KEY, VALUE>>) =
        throw UnsupportedOperationException()

    override fun addAll(elements: Collection<Entry<KEY, VALUE>>) =
        list.addAll(elements)

    override fun addAll(index: Int, elements: Collection<Entry<KEY, VALUE>>) =
        throw UnsupportedOperationException()

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Entry<KEY, VALUE>> =
        throw UnsupportedOperationException()

    override fun listIterator() = list.listIterator()

    override fun listIterator(index: Int) = list.listIterator(index)

    override fun iterator() = list.iterator()

    fun iteratorKeys() = object : MutableIterator<KEY> {
        val iterator = this@ListEntry.iterator()
        override fun hasNext() = iterator.hasNext()

        override fun next() = iterator.next().key

        override fun remove() = iterator.remove()
    }

    fun iterableKeys() = Iterable { iteratorKeys() }

    fun iteratorValues() = object : MutableIterator<VALUE> {
        val iterator = this@ListEntry.iterator()
        override fun hasNext() = iterator.hasNext()

        override fun next() = iterator.next().value

        override fun remove() = iterator.remove()
    }

    fun iterableValues() = Iterable { iteratorValues() }

}


