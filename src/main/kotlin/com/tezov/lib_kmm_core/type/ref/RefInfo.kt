


package com.tezov.lib_kmm_core.type.ref

import kotlin.reflect.KClass

class RefInfo<T : Any>(ref: T) {
    val type: KClass<out T> = ref::class
    private val hashcode: Int = ref.hashCode()

    fun isTypeEqual(other: Any) = type == other::class

    fun isTypeEqual(type: KClass<out T>) = this.type == type

    override fun hashCode() = hashcode

    override fun equals(other: Any?) = other?.let {
        if (other is RefInfo<*>) {
            hashcode == other.hashcode
        } else {
            hashcode == other.hashCode()
        }
    } ?: false

}