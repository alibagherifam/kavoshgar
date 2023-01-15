package com.alibagherifam.kavoshgar.lobby

import com.alibagherifam.kavoshgar.Constants
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

class KavoshgarClient {
    private var discoverySocket: DatagramSocket? = null
    private lateinit var discoveryPacket: DatagramPacket
    private lateinit var serverReplyPacket: DatagramPacket

    suspend fun startDiscovery() {
        withContext(Dispatchers.IO) {
            try {
                openSocket()
                while (true) {
                    // Log.i("LAN", "Client: Sending discovery...")
                    discoverySocket!!.send(discoveryPacket)
                    // Log.i("LAN", "Client: Discovery sent!")
                    delay(Constants.DISCOVERY_INTERVALS)
                }
            } finally {
                closeSocket()
            }
        }
    }

    /* TODO: Remove checking whether socket is open and also catching
        closed socket exception. We should join both calls together somehow
        and using one try/finally block
     */
    fun discoveredServerFlow(): Flow<ServerInformation> = flow {
        while (true) {
            if (discoverySocket == null) {
                delay(100)
                continue
            }
            // Log.i("LAN", "Client: Receiving discovery reply...")
            discoverySocket!!.receive(serverReplyPacket)
            // Log.i("LAN", "Client: Discovery reply received!")
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
        // Log.i("LAN", "Client: Discovery socket created!")
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

    private fun calculateLatency(destinationAddress: InetAddress): Int {
        val currentTime = System.currentTimeMillis()
        val timeOut = Constants.PING_TIME_OUT.toInt()
        return if (destinationAddress.isReachable(timeOut)) {
            (System.currentTimeMillis() - currentTime).toInt()
        } else {
            -1
        }
    }
}
