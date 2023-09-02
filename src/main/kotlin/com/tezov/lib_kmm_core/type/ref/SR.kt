


package com.tezov.lib_kmm_core.type.ref

//Strong
class SR<T : Any>(ref: T? = null) : Ref<T>() {
    private var ref: T? = null

    override var value: T?
        get() = ref
        set(value) {
            updateInfo(value)
            this.ref = value
        }

    init {
        value = ref
    }


}