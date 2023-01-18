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
import kotlinx.coroutines.yield
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.system.measureTimeMillis

/**
 * A client that constantly broadcasts server discovery packets over the network
 * and simultaneously receives any potential [ServerInformation].
 */
class KavoshgarClient {
    companion object {
        private const val TAG = "Client"
    }

    private var discoverySocket: DatagramSocket? = null
    private lateinit var serverDiscoveryPacket: DatagramPacket
    private lateinit var serverInformationPacket: DatagramPacket

    /**
     * Starts broadcasting server discovery packets and awaits for [ServerInformation]
     * in an infinite loop until the caller scope gets canceled.
     *
     * @return a [Flow] that emits receiving service [information][ServerInformation].
     */
    fun startServerDiscovery(): Flow<ServerInformation> = flow {
        openSocket()
        coroutineScope {
            launch {
                while (true) {
                    broadcastServerDiscovery()
                    delay(Constants.DISCOVERY_INTERVALS)
                }
            }
            launch {
                while (true) {
                    emit(awaitServerResponse().asServerInformation())
                    flushServerInformationPacket()
                    yield()
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
            serverDiscoveryPacket = DatagramPacket(
                message,
                message.size,
                InetAddress.getByName(Constants.BROADCAST_ADDRESS),
                Constants.DISCOVERY_PORT
            )
            serverInformationPacket = DatagramPacket(
                ByteArray(Constants.SERVER_NAME_MAX_SIZE),
                Constants.SERVER_NAME_MAX_SIZE
            )
            discoverySocket = DatagramSocket().apply {
                broadcast = true
            }
            Log.i(TAG, message = "Discovery socket created!")
        }
    }

    private suspend fun broadcastServerDiscovery() {
        withContext(Dispatchers.IO) {
            Log.i(TAG, message = "Broadcasting server discovery...")
            discoverySocket!!.send(serverDiscoveryPacket)
            Log.i(TAG, message = "Server discovery is broadcast!")
        }
    }

    private suspend fun awaitServerResponse(): DatagramPacket {
        return withContext(Dispatchers.IO) {
            Log.i(TAG, message = "Awaiting server information...")
            discoverySocket!!.receive(serverInformationPacket)
            Log.i(TAG, message = "Server information received!")
            serverInformationPacket
        }
    }

    private suspend fun DatagramPacket.asServerInformation() = ServerInformation(
        name = String(data, 0, length),
        address = address,
        latency = calculateLatency(address)
    )

    private suspend fun calculateLatency(destinationAddress: InetAddress): Long {
        return withContext(Dispatchers.IO) {
            val isReachable: Boolean
            val latency = measureTimeMillis {
                isReachable = destinationAddress.isReachable(Constants.PING_TIMEOUT)
            }
            if (isReachable) latency else -1
        }
    }

    private fun flushServerInformationPacket() {
        serverInformationPacket.length = Constants.SERVER_NAME_MAX_SIZE
    }

    private suspend fun closeSocket() {
        withContext(Dispatchers.IO) {
            discoverySocket?.close()
            discoverySocket = null
        }
    }
}
