package com.alibagherifam.kavoshgar.lobby

import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.logger.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException

/**
 * This client constantly broadcasts advertisement packets over the network and
 * simultaneously listens to potential [servers'][KavoshgarServer] replies.
 */
class KavoshgarClient {
    private var discoverySocket: DatagramSocket? = null
    private lateinit var discoveryPacket: DatagramPacket
    private lateinit var serverReplyPacket: DatagramPacket

    /**
     * Broadcasts advertisement packets in an infinite loop until the caller scope
     * gets canceled. This function is main-safe and runs on the background thread.
     */
    suspend fun startDiscovery() {
        withContext(Dispatchers.IO) {
            try {
                openSocket()
                while (true) {
                    Log.i(tag = "Client", message = "Client: Sending discovery...")
                    discoverySocket!!.send(discoveryPacket)
                    Log.i(tag = "Client", message = "Discovery sent!")
                    delay(Constants.DISCOVERY_INTERVALS)
                }
            } finally {
                closeSocket()
            }
        }
    }

    /* TODO: Remove checking whether socket is open and also catching
        closed socket exception. We should join both startDiscovery and
        this functions together somehow and using one try/finally block
     */
    /**
     * Listens for potential [server][KavoshgarServer] replies and notifies the caller
     * about the [information][ServerInformation] of responding servers in a reactive way.
     * @return a [Flow] that emits [information][ServerInformation] of discovered servers.
     */
    fun discoveredServerFlow(): Flow<ServerInformation> = flow {
        while (true) {
            if (discoverySocket == null) {
                delay(100)
                continue
            }
            Log.i(tag = "Client", message = "Client: Receiving discovery reply...")
            discoverySocket!!.receive(serverReplyPacket)
            Log.i(tag = "Client", message = "Client: Discovery reply received!")
            emit(mapToLobby(serverReplyPacket))
            serverReplyPacket.length = Constants.LOBBY_NAME_MAX_SIZE
        }
    }.catch {
        if (it !is SocketException) {
            throw it
        }
    }.flowOn(Dispatchers.IO)

    private fun openSocket() {
        if (discoverySocket != null) {
            return
        }
        val message = "A".toByteArray()
        discoveryPacket = DatagramPacket(
            message,
            message.size,
            InetAddress.getByName(Constants.BROADCAST_ADDRESS),
            Constants.LOBBY_DISCOVERY_PORT
        )
        serverReplyPacket = DatagramPacket(
            ByteArray(Constants.LOBBY_NAME_MAX_SIZE),
            Constants.LOBBY_NAME_MAX_SIZE
        )
        discoverySocket = DatagramSocket().apply {
            broadcast = true
        }
        Log.i(tag = "Client", message = "Client: Discovery socket created!")
    }

    private fun closeSocket() {
        discoverySocket?.close()
        discoverySocket = null
    }

    private fun mapToLobby(replyPacket: DatagramPacket) = ServerInformation(
        name = String(replyPacket.data, 0, replyPacket.length),
        address = replyPacket.address,
        latency = calculateLatency(replyPacket.address)
    )

    private fun calculateLatency(destinationAddress: InetAddress): Long {
        val currentTime = System.currentTimeMillis()
        val timeOut = Constants.PING_TIME_OUT.toInt()
        return if (destinationAddress.isReachable(timeOut)) {
            (System.currentTimeMillis() - currentTime)
        } else {
            -1
        }
    }
}
