

package com.tezov.lib_kmm_core.type.primitive

import com.tezov.lib_kmm_core.type.primitive.IntTo.toStringHex

object ObjectTo {

    fun Any?.hashcodeIdentity() = this?.let { System.identityHashCode(this) }

    fun Any?.hashcodeIdentityString() =
        this?.let { "0x" + System.identityHashCode(this).toStringHex() }?.let { "object is null" }
}