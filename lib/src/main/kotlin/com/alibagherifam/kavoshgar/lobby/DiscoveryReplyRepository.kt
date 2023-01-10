package com.alibagherifam.kavoshgar.lobby

import com.alibagherifam.kavoshgar.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress

class DiscoveryReplyRepository(lobbyName: String) {
    private var discoveryReplySocket: DatagramSocket? = null
    private lateinit var receivedPacket: DatagramPacket
    private val replyMessage = lobbyName.toByteArray()

    suspend fun startDiscoveryReplying() {
        withContext(Dispatchers.IO) {
            try {
                openSocket()
                while (true) {
                    receiveDiscovery()
                    yield()
                    replyToDiscovery(receivedPacket.socketAddress)
                }
            } finally {
                closeSocket()
            }
        }
    }

    private fun receiveDiscovery() {
        // Log.i("LAN", "Server: Receiving discovery...")
        discoveryReplySocket!!.receive(receivedPacket)
        // Log.i("LAN", "Server: Discovery received!")
        receivedPacket.length = Constants.DISCOVERY_PACKET_SIZE
    }

    private fun replyToDiscovery(destinationAddress: SocketAddress) {
        val replyPacket = DatagramPacket(
            replyMessage,
            replyMessage.size,
            destinationAddress
        )
        // Log.i("LAN", "Server: Sending discovery reply...")
        discoveryReplySocket!!.send(replyPacket)
        // Log.i("LAN", "Server: Discovery reply sent!")
    }

    private fun openSocket() {
        if (discoveryReplySocket != null) {
            return
        }
        receivedPacket = DatagramPacket(
            ByteArray(Constants.DISCOVERY_PACKET_SIZE),
            Constants.DISCOVERY_PACKET_SIZE
        )
        discoveryReplySocket = DatagramSocket(Constants.LOBBY_DISCOVERY_PORT)
        // Log.i("LAN", "Server: Discovery reply socket created!")
    }

    private fun closeSocket() {
        discoveryReplySocket?.close()
        discoveryReplySocket = null
    }
}
