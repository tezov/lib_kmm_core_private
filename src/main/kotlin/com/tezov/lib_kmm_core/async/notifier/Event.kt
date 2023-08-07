

package com.tezov.lib_kmm_core.async.notifier

interface Event{

    object Close : Event
    object Cancel : Event
    object Confirm : Event
    object OwnerShipLost : Event

}