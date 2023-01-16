package com.alibagherifam.kavoshgar.lobby

import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.logger.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress

/**
 * A server that constantly listens for [client][KavoshgarClient] advertisements and
 * replies to them to notify clients about its presence and IP address.
 *
 * @param[serverName] an arbitrary name will be shown to clients.
 */
class KavoshgarServer(serverName: String) {
    private var discoveryReplySocket: DatagramSocket? = null
    private lateinit var receivedPacket: DatagramPacket
    private val replyMessage = serverName.toByteArray()

    /**
     * Listening to [client][KavoshgarClient] advertisements and replying to
     * them in an infinite loop until the caller scope gets canceled. This function
     * is main-safe runs on the background thread.
     */
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
        Log.i(tag = "Server", message = "Server: Receiving discovery...")
        discoveryReplySocket!!.receive(receivedPacket)
        Log.i(tag = "Server", message = " Server : Discovery received!")
        receivedPacket.length = Constants.DISCOVERY_PACKET_SIZE
    }

    private fun replyToDiscovery(destinationAddress: SocketAddress) {
        val replyPacket = DatagramPacket(
            replyMessage,
            replyMessage.size,
            destinationAddress
        )
        Log.i(tag = "Server", message = "Server: Sending discovery reply...")
        discoveryReplySocket!!.send(replyPacket)
        Log.i(tag = "Server", message = "Server: Discovery reply sent!")
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
        Log.i(tag = "Server", message = "Server: Discovery reply socket created!")
    }

    private fun closeSocket() {
        discoveryReplySocket?.close()
        discoveryReplySocket = null
    }
}
