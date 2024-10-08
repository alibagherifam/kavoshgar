package dev.alibagherifam.kavoshgar.discovery

import dev.alibagherifam.kavoshgar.Constants
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
import de.halfbit.logger.i as logInfo

/**
 * A client constantly listens to the network to discover available servers.
 */
class KavoshgarClient {
    private var discoverySocket: DatagramSocket? = null
    private lateinit var serverAdvertisementPacket: DatagramPacket

    /**
     * Starts awaiting server advertisement in an infinite loop
     * until the caller scope gets canceled.
     *
     * @return a [Flow] that emits discovered server's [information][ServerInformation].
     */
    fun startServerDiscovery(): Flow<ServerInformation> = flow {
        openSocket()
        while (true) {
            emit(awaitServerAdvertisement().asServerInformation())
            flushServerAdvertisementPacket()
            yield()
        }
    }.onCompletion { closeSocket() }

    private suspend fun openSocket() {
        if (discoverySocket != null) {
            return
        }
        withContext(Dispatchers.IO) {
            serverAdvertisementPacket = DatagramPacket(
                ByteArray(Constants.SERVER_NAME_MAX_SIZE),
                Constants.SERVER_NAME_MAX_SIZE
            )
            discoverySocket = DatagramSocket(Constants.ADVERTISEMENT_PORT)
            logInfo(TAG) { "Discovery socket created!" }
        }
    }

    private suspend fun awaitServerAdvertisement(): DatagramPacket {
        return withContext(Dispatchers.IO) {
            logInfo(TAG) { "Awaiting server advertisement..." }
            discoverySocket!!.receive(serverAdvertisementPacket)
            logInfo(TAG) { "Server advertisement received!" }
            serverAdvertisementPacket
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

    private fun flushServerAdvertisementPacket() {
        serverAdvertisementPacket.length = Constants.SERVER_NAME_MAX_SIZE
    }

    private suspend fun closeSocket() {
        withContext(Dispatchers.IO) {
            discoverySocket?.close()
            discoverySocket = null
        }
    }

    companion object {
        private const val TAG = "Client"
    }
}
