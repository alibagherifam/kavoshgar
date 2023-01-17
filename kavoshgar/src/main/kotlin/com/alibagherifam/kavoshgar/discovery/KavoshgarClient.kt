package com.alibagherifam.kavoshgar.discovery

import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.logger.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * This client constantly broadcasts advertisement packets over the network and
 * simultaneously listens to potential [server][KavoshgarServer] replies.
 */
class KavoshgarClient {
    companion object {
        private const val TAG = "Client"
    }

    private var discoverySocket: DatagramSocket? = null
    private lateinit var advertisementPacket: DatagramPacket
    private lateinit var serverResponsePacket: DatagramPacket

    /**
     * Broadcasts advertisement packets and listens for potential [server][KavoshgarServer] replies
     * in an infinite loop until the caller scope gets canceled. Notifies the caller about the
     * [information][ServerInformation] of responding servers in a reactive way.
     * @return a [Flow] that emits [information][ServerInformation] of discovered servers.
     */
    fun startDiscovery(): Flow<ServerInformation> = flow {
        openSocket()
        coroutineScope {
            val socket = discoverySocket!!
            launch {
                while (true) {
                    socket.broadcastAdvertisement()
                    delay(Constants.DISCOVERY_INTERVALS)
                }
            }
            launch {
                while (true) {
                    emit(socket.receiveResponse().asServerInformation())
                    clearResponsePacket()
                }
            }
        }
    }.onCompletion { closeSocket() }

    private suspend fun openSocket() {
        if (discoverySocket != null) {
            return
        }
        withContext(Dispatchers.IO) {
            val message = "A".toByteArray()
            advertisementPacket = DatagramPacket(
                message,
                message.size,
                InetAddress.getByName(Constants.BROADCAST_ADDRESS),
                Constants.LOBBY_DISCOVERY_PORT
            )
            serverResponsePacket = DatagramPacket(
                ByteArray(Constants.LOBBY_NAME_MAX_SIZE),
                Constants.LOBBY_NAME_MAX_SIZE
            )
            discoverySocket = DatagramSocket().apply {
                broadcast = true
            }
            Log.i(TAG, message = "Discovery socket created!")
        }
    }

    private suspend fun closeSocket() {
        withContext(Dispatchers.IO) {
            discoverySocket?.close()
            discoverySocket = null
        }
    }

    private fun calculateLatency(destinationAddress: InetAddress): Long {
        val currentTime = System.currentTimeMillis()
        val timeOut = Constants.PING_TIME_OUT.toInt()
        return if (destinationAddress.isReachable(timeOut)) {
            (System.currentTimeMillis() - currentTime)
        } else {
            -1
        }
    }

    private suspend fun DatagramSocket.broadcastAdvertisement() {
        withContext(Dispatchers.IO) {
            Log.i(TAG, message = "Broadcasting advertisement...")
            send(advertisementPacket)
            Log.i(TAG, message = "Advertisement broadcasted!")
        }
    }

    private suspend fun DatagramSocket.receiveResponse(): DatagramPacket {
        return withContext(Dispatchers.IO) {
            Log.i(TAG, message = "Listening for discovery response...")
            receive(serverResponsePacket)
            Log.i(TAG, message = "Discovery response received!")
            serverResponsePacket
        }
    }

    private fun DatagramPacket.asServerInformation() = ServerInformation(
        name = String(data, 0, length),
        address = address,
        latency = calculateLatency(address)
    )

    private fun clearResponsePacket() {
        serverResponsePacket.length = Constants.LOBBY_NAME_MAX_SIZE
    }
}
