


package com.tezov.lib_kmm_core.async.notifier.observable

import com.tezov.lib_kmm_core.async.notifier.Notifier
import com.tezov.lib_kmm_core.async.notifier.Notifier.Packet
import com.tezov.lib_kmm_core.async.notifier.NotifierAny
import com.tezov.lib_kmm_core.type.collection.ListOrObject
import com.tezov.lib_kmm_core.type.ref.Ref.Companion.value
import com.tezov.lib_kmm_core.type.ref.WR

abstract class Observable<EVENT : Any, PACKET : Packet<EVENT>> : Notifier.Observable<EVENT, PACKET> {
    private val accessList = ListOrObject<PACKET>()
    private var notifierWR: WR<NotifierAny>? = null

    @Suppress("UNCHECKED_CAST")
    internal fun newDispatcher(): (packet: Packet<*>) -> Unit = {
        notifierWR.value?.dispatchEvent(it as Packet<Any>)
    }

    override fun attachTo(notifier: NotifierAny) {
        notifierWR = WR(notifier)
    }

    override fun isPacketValid(packet: PACKET): Boolean =
        synchronized(this) { getPacket(packet.event) == packet }

    override fun getPacket(event: EVENT?): PACKET? = synchronized(this) {
        accessList.get()?.takeIf {
            it.event == event
        }
    }

    override fun obtainPacket(event: EVENT?): PACKET = synchronized(this) {
        accessList.get()?.takeIf {
            it.event == event
        } ?: createPacket(event).also {
            accessList.set(it)
        }
    }

    override fun getPacketList(): List<PACKET> =
        synchronized(this) { accessList }

    override fun clearPacket() = synchronized(this) { accessList.clear() }

    protected abstract fun createPacket(event: EVENT?): PACKET

}