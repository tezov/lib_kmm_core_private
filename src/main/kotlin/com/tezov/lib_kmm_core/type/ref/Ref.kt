


package com.tezov.lib_kmm_core.type.ref

import com.tezov.lib_kmm_core.extension.ExtensionNull.isNotNull
import com.tezov.lib_kmm_core.extension.ExtensionNull.isNull
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

abstract class Ref<T : Any> protected constructor() {
    var info: RefInfo<T>? = null
        private set

    protected fun updateInfo(referent: T?) {
        info = referent?.let {
            RefInfo(referent)
        }
    }

    val type get() = info?.type

    override fun hashCode() = info?.hashCode() ?: 0

    override fun equals(other: Any?) =
        if (other is Ref<*>) {
            if (other.value.isNotNull && value.isNotNull) {
                other.hashCode() == hashCode()
            } else {
                other.value.isNull && value.isNull
            }
        } else {
            if (other.isNotNull && value.isNotNull) {
                this.hashCode() == other.hashCode()
            } else {
                other.isNull && value.isNull
            }
        }

    abstract var value:T?

    inner class LostRef<T : Any>(t: T, q: ReferenceQueue<T>?) : WeakReference<T>(t, q) {

        override fun hashCode() = this@Ref.hashCode()

        override fun equals(other: Any?) = info.hashCode() == other.hashCode()
    }

    companion object {

        val <T : Any> Ref<T>?.value get() = this?.value

        val <T : Any> Ref<T>?.type get() = this?.type

    }
}