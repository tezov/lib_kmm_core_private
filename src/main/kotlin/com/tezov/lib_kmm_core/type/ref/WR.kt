


package com.tezov.lib_kmm_core.type.ref

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

//Weak
class WR<T : Any>(ref: T? = null, q: ReferenceQueue<T>? = null) : Ref<T>() {
    private var ref: WeakReference<T>? = null

    override var value: T?
        get() = ref?.get()
        set(value) {
            value(value, null)
        }

    init {
        value(ref, q)
    }

    fun value(ref: T?, q: ReferenceQueue<T>? = null) {
        updateInfo(ref)
        this.ref = ref?.let {
            q?.let {
                LostRef(ref, q)
            } ?: run {
                WeakReference(ref)
            }
        }
    }

}