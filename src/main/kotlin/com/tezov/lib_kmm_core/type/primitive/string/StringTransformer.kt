

package com.tezov.lib_kmm_core.type.primitive.string

interface StringTransformer {

    fun alter(s: String?): String?
    fun restore(s: String?): String?

}