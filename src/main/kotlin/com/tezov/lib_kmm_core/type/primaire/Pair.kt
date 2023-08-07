

package com.tezov.lib_kmm_core.type.primaire

class Pair<F, S>(var first: F, var second: S) {

    override fun equals(other: Any?): Boolean {
        return if (other is Pair<*, *>) {
            var result = equals(first, other.first)
            result = result and equals(second, other.second)
            result
        } else {
            false
        }
    }

    private fun equals(o1: Any?, o2: Any?): Boolean {
        return when {
            o1 != null -> o1 == o2
            else -> o2 == null
        }
    }
}