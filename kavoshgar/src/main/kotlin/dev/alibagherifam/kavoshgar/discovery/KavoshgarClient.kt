package dev.alibagherifam.kavoshgar.discovery

import dev.alibagherifam.kavoshgar.Constants
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.SocketAddress
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.toJavaAddress
import io.ktor.util.network.address
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlinx.io.readByteArray
import java.net.InetAddress
import kotlin.system.measureTimeMillis
import de.halfbit.logger.i as logInfo

/**
 * A client constantly listens to the network to discover available servers.
 */
class KavoshgarClient {
    private var discoverySocket: BoundDatagramSocket? = null

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
            yield()
        }
    }.onCompletion { closeSocket() }

    private suspend fun openSocket() {
        check(discoverySocket == null) { "Discovery socket is already opened!" }
        withContext(Dispatchers.IO) {
            discoverySocket =
                aSocket(SelectorManager(Dispatchers.IO))
                    .udp()
                    .bind(port = Constants.ADVERTISEMENT_PORT)
        }
        logInfo(TAG) { "Discovery socket created!" }
    }

    private suspend fun awaitServerAdvertisement(): Datagram {
        val socket = checkNotNull(discoverySocket) { "Discovery socket is not opened!" }
        logInfo(TAG) { "Awaiting server advertisement..." }
        return withContext(Dispatchers.IO) {
            socket.receive()
        }.also {
            logInfo(TAG) { "Server advertisement received!" }
        }
    }

    private suspend fun Datagram.extractServerInformation(): ServerInformation {
        val address = address.toInetAddress()
        return ServerInformation(
            address = address,
            latency = calculateLatency(address),
            payload = packet.readByteArray()
        )
    }

    private fun SocketAddress.toInetAddress() =
        this.toJavaAddress()
            .let { InetAddress.getByName(it.address) }

    private suspend fun calculateLatency(destinationAddress: InetAddress): Long =
        withContext(Dispatchers.IO) {
            val isReachable: Boolean
            val latency = measureTimeMillis {
                isReachable = destinationAddress.isReachable(Constants.PING_TIMEOUT)
            }
            if (isReachable) latency else -1
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
