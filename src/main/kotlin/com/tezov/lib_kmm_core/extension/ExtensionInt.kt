

package com.tezov.lib_kmm_core.extension


object ExtensionInt {

    fun Int.isEven() = this % 2 == 0

    fun <T> Int.onEven(run: (Int) -> T) = if (isEven()) {
        run(this)
    } else null

    fun Int.isOdd() = this % 2 == 1
    fun <T> Int.onOdd(run: (Int) -> T) = if (isOdd()) {
        run(this)
    } else null

    fun <T> Int.action(ifEven: (Int) -> T, ifOdd: (Int) -> T) = if (isEven()) {
        ifEven(this)
    } else {
        ifOdd(this)
    }
}