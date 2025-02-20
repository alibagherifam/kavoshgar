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
            val packet = awaitServerAdvertisement()
            emit(packet.extractServerInformation())
            flushServerAdvertisementPacket()
            yield()
        }
    }.onCompletion { closeSocket() }

    private suspend fun openSocket() {
        check(discoverySocket == null) { "Discovery socket is already opened!" }
        withContext(Dispatchers.IO) {
            serverAdvertisementPacket = DatagramPacket(
                ByteArray(Constants.SERVER_NAME_MAX_SIZE),
                Constants.SERVER_NAME_MAX_SIZE
            )
            discoverySocket = DatagramSocket(Constants.ADVERTISEMENT_PORT)
        }
        logInfo(TAG) { "Discovery socket created!" }
    }

    private suspend fun awaitServerAdvertisement(): DatagramPacket {
        val socket = checkNotNull(discoverySocket) { "Discovery socket is not opened!" }
        logInfo(TAG) { "Awaiting server advertisement..." }
        return withContext(Dispatchers.IO) {
            socket.receive(serverAdvertisementPacket)
            serverAdvertisementPacket
        }.also {
            logInfo(TAG) { "Server advertisement received!" }
        }
    }

    private suspend fun DatagramPacket.extractServerInformation() =
        ServerInformation(
            name = String(data, 0, length),
            address = address,
            latency = calculateLatency(address)
        )

    private suspend fun calculateLatency(destinationAddress: InetAddress): Long =
        withContext(Dispatchers.IO) {
            val isReachable: Boolean
            val latency = measureTimeMillis {
                isReachable = destinationAddress.isReachable(Constants.PING_TIMEOUT)
            }
            if (isReachable) latency else -1
        }

    private fun flushServerAdvertisementPacket() {
        serverAdvertisementPacket.length = Constants.SERVER_NAME_MAX_SIZE
    }

    private suspend fun closeSocket() {
        val socket = checkNotNull(discoverySocket) { "Discovery socket is not opened!" }
        withContext(Dispatchers.IO) {
            socket.close()
        }
        discoverySocket = null
    }

    companion object {
        private const val TAG = "Client"
    }
}
