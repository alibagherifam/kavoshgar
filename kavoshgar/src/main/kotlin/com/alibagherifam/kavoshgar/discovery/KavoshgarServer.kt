package com.alibagherifam.kavoshgar.discovery

import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.logger.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketAddress

/**
 * A server that constantly listens for [client's][KavoshgarClient] discoveries and
 * responds to them to advertise the presence of itself.
 *
 * @param[serverName] an arbitrary name will be shown to clients.
 */
class KavoshgarServer(private val serverName: String) {
    companion object {
        private const val TAG = "Client"
    }

    private var advertismentSocket: DatagramSocket? = null
    private lateinit var serverDiscoveryPacket: DatagramPacket
    private lateinit var serverInformationPacket: DatagramPacket

    /**
     * Starts listening to [client's][KavoshgarClient] discoveries and responds to them
     *  in an infinite loop until the caller scope gets canceled. This function is main-safe.
     */
    suspend fun advertisePresence() {
        try {
            openSocket()
            while (true) {
                val clientAddress = awaitServerDiscovery().socketAddress
                sendServerInformation(clientAddress)
                flushDiscoveryPacket()
                yield()
            }
        } finally {
            closeSocket()
        }
    }

    private suspend fun openSocket() {
        if (advertismentSocket != null) {
            return
        }
        withContext(Dispatchers.IO) {
            serverDiscoveryPacket = DatagramPacket(
                ByteArray(Constants.DISCOVERY_PACKET_SIZE),
                Constants.DISCOVERY_PACKET_SIZE
            )
            serverInformationPacket = serverName
                .toByteArray().let { DatagramPacket(it, it.size) }
            advertismentSocket = DatagramSocket(Constants.DISCOVERY_PORT)
            Log.i(TAG, message = "Advertisment socket created!")
        }
    }

    private suspend fun awaitServerDiscovery(): DatagramPacket {
        return withContext(Dispatchers.IO) {
            Log.i(TAG, message = "Awaiting server discovery from client...")
            advertismentSocket!!.receive(serverDiscoveryPacket)
            Log.i(TAG, message = "Server discovery received!")
            serverDiscoveryPacket
        }
    }

    private suspend fun sendServerInformation(clientAddress: SocketAddress) {
        withContext(Dispatchers.IO) {
            serverInformationPacket.socketAddress = clientAddress
            Log.i(TAG, message = "Sending server information to client...")
            advertismentSocket!!.send(serverInformationPacket)
            Log.i(TAG, message = "Server information sent!")
        }
    }

    private fun flushDiscoveryPacket() {
        serverDiscoveryPacket.length = Constants.DISCOVERY_PACKET_SIZE
    }

    private suspend fun closeSocket() {
        withContext(Dispatchers.IO) {
            advertismentSocket?.close()
            advertismentSocket = null
        }
    }
}
