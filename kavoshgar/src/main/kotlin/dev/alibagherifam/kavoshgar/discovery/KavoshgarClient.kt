package dev.alibagherifam.kavoshgar.discovery

import dev.alibagherifam.kavoshgar.Constants
import dev.alibagherifam.kavoshgar.logger.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.system.measureTimeMillis

/**
 * A client constantly listens to the network to discover available servers.
 */
class KavoshgarClient {
    companion object {
        private const val TAG = "Client"
    }

    private var discoverySocket: DatagramSocket? = null
    private lateinit var serverAdvertismentPacket: DatagramPacket

    /**
     * Starts awaiting server advertisement in an infinite loop
     * until the caller scope gets canceled.
     *
     * @return a [Flow] that emits discovered server's [information][ServerInformation].
     */
    fun startServerDiscovery(): Flow<ServerInformation> = flow {
        openSocket()
        while (true) {
            emit(awaitServerAdvertisment().asServerInformation())
            flushServerAdvertismentPacket()
            yield()
        }
    }.onCompletion { closeSocket() }

    private suspend fun openSocket() {
        if (discoverySocket != null) {
            return
        }
        withContext(Dispatchers.IO) {
            serverAdvertismentPacket = DatagramPacket(
                ByteArray(Constants.SERVER_NAME_MAX_SIZE),
                Constants.SERVER_NAME_MAX_SIZE
            )
            discoverySocket = DatagramSocket(Constants.ADVERTISMENT_PORT)
            Log.i(TAG, message = "Discovery socket created!")
        }
    }

    private suspend fun awaitServerAdvertisment(): DatagramPacket {
        return withContext(Dispatchers.IO) {
            Log.i(TAG, message = "Awaiting server advertisment...")
            discoverySocket!!.receive(serverAdvertismentPacket)
            Log.i(TAG, message = "Server advertisment received!")
            serverAdvertismentPacket
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

    private fun flushServerAdvertismentPacket() {
        serverAdvertismentPacket.length = Constants.SERVER_NAME_MAX_SIZE
    }

    private suspend fun closeSocket() {
        withContext(Dispatchers.IO) {
            discoverySocket?.close()
            discoverySocket = null
        }
    }
}
