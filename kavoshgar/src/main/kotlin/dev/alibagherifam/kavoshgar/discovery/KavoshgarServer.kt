package dev.alibagherifam.kavoshgar.discovery

import dev.alibagherifam.kavoshgar.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import de.halfbit.logger.i as logInfo

/**
 * A server that constantly advertises its presence information for
 * any potential [client][KavoshgarClient] listening to the network.
 *
 * @param[serverName] an arbitrary name will be shown to clients.
 */
class KavoshgarServer(private val serverName: String) {
    init {
        require(serverName.toByteArray().size < Constants.SERVER_NAME_MAX_SIZE) {
            "Server name cannot be larger than ${Constants.SERVER_NAME_MAX_SIZE} bytes."
        }
    }

    private var advertisementSocket: DatagramSocket? = null
    private lateinit var advertisementPacket: DatagramPacket

    /**
     * Starts broadcasting the server's presence packet over the network
     * in an infinite loop until the caller scope gets canceled.
     * This function is main-safe.
     */
    suspend fun advertisePresence() {
        if (advertisementSocket != null) {
            return
        }
        try {
            openSocket()
            while (true) {
                broadcastAdvertisementPacket()
                delay(Constants.ADVERTISEMENT_INTERVALS)
            }
        } finally {
            closeSocket()
        }
    }

    private suspend fun openSocket() {
        withContext(Dispatchers.IO) {
            advertisementPacket = buildAdvertisementPacket()

            advertisementSocket = DatagramSocket().apply {
                broadcast = true
            }
            logInfo(TAG) { "Advertisement socket created!" }
        }
    }

    private fun buildAdvertisementPacket(): DatagramPacket {
        val data = serverName.toByteArray()
        return DatagramPacket(
            data,
            data.size,
            InetAddress.getByName(Constants.BROADCAST_ADDRESS),
            Constants.ADVERTISEMENT_PORT
        )
    }

    private suspend fun broadcastAdvertisementPacket() {
        withContext(Dispatchers.IO) {
            val socket = checkNotNull(advertisementSocket) { "Socket is not opened yet!" }
            socket.send(advertisementPacket)
            logInfo(TAG) { "Server information broadcast!" }
        }
    }

    private suspend fun closeSocket() {
        withContext(Dispatchers.IO) {
            advertisementSocket?.close()
            advertisementSocket = null
        }
    }

    companion object {
        private const val TAG = "Server"
    }
}
