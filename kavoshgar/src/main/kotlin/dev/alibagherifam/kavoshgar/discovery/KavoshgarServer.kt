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
    private var advertisementSocket: DatagramSocket? = null
    private lateinit var serverInformationPacket: DatagramPacket

    /**
     * Starts broadcasting the server's presence information over
     * the network in an infinite loop until the caller scope gets canceled.
     * This function is main-safe.
     */
    suspend fun advertisePresence() {
        try {
            openSocket()
            while (true) {
                broadcastServerInformation()
                delay(Constants.ADVERTISEMENT_INTERVALS)
            }
        } finally {
            closeSocket()
        }
    }

    private suspend fun openSocket() {
        if (advertisementSocket != null) {
            return
        }
        withContext(Dispatchers.IO) {
            val data = serverName.toByteArray()
            serverInformationPacket = DatagramPacket(
                data,
                data.size,
                InetAddress.getByName(Constants.BROADCAST_ADDRESS),
                Constants.ADVERTISEMENT_PORT
            )
            advertisementSocket = DatagramSocket().apply {
                broadcast = true
            }
            logInfo(TAG) { "Advertisement socket created!" }
        }
    }

    private suspend fun broadcastServerInformation() {
        withContext(Dispatchers.IO) {
            logInfo(TAG) { "Broadcasting server information..." }
            advertisementSocket!!.send(serverInformationPacket)
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
