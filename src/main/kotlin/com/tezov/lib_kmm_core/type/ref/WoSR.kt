


package com.tezov.lib_kmm_core.type.ref

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

//Weak or Strong
class WoSR<T:Any> (ref: T? = null, keepAsWeakRef: Boolean = true, private var q: ReferenceQueue<T>? = null): Ref<T>() {
    private var ref: Any? = null

    @Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
    override var value: T?
        get() = ((ref as? WeakReference<*>)?.get() ?: ref) as T?
        set(value) {
            throw UnsupportedOperationException()
        }

    init {
        value(ref, keepAsWeakRef, q)
    }

    val isWeak get() = ref is WeakReference<*>

    val isStrong get() = ref != null && ref !is WeakReference<*>

    fun makeWeak() {
        if(isStrong){
            value?.let { currentRef ->
                ref = q?.let {
                    LostRef(currentRef, q)
                } ?: run {
                    WeakReference(currentRef)
                }
            }
        }
    }

    fun makeStrong() {
        if(isWeak){
            value?.let { currentRef ->
                ref = currentRef
            }
        }
    }

    fun value(ref: T?, keepAsWeakRef: Boolean = true, q: ReferenceQueue<T>? = null) {
        updateInfo(ref)
        this.q = q
        this.ref = ref
        if(keepAsWeakRef){
            makeWeak()
        }
    }

}