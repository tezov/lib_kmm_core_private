


package com.tezov.lib_kmm_core.type.collection

import java.util.concurrent.ConcurrentLinkedDeque

class ConcurrentLinkedSet<V> : ConcurrentLinkedDeque<V>() {

    override fun add(element: V): Boolean {
        this.forEach {
            if (it == element) return false
        }
        return super.add(element)
    }

    override fun remove(element: V?): Boolean {
        return super.remove(element)
    }

}