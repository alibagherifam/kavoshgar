package dev.alibagherifam.kavoshgar.messenger

import dev.alibagherifam.kavoshgar.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.Socket
import de.halfbit.logger.i as logInfo

class MessengerService(private val socketProvider: SocketProvider) {
    private var messagingSocket: Socket? = null
    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter

    suspend fun sendMessage(message: String) {
        withContext(Dispatchers.IO) {
            logInfo(TAG) { "Sending message..." }
            writer.run {
                write(message)
                newLine()
                flush()
            }
            logInfo(TAG) { "Message sent!" }
        }
    }

    /* TODO: Change isConnected signaling mechanism (Perhaps
        we should move [KavoshgarServer] into [MessagingService])
    * */
    fun receiveMessages(): Flow<String> = flow {
        connect()
        emit("")
        while (true) {
            readMessage()?.let { emit(it) }
            delay(Constants.MESSAGING_INTERVALS)
        }
    }.onCompletion { disconnect() }

    private suspend fun readMessage(): String? {
        return withContext(Dispatchers.IO) {
            logInfo(TAG) { "Receiving message..." }
            val message = reader.readLine()
            logInfo(TAG) { "Message received!" }
            message
        }
    }

    private suspend fun connect() {
        val socket = messagingSocket ?: return
        withContext(Dispatchers.IO) {
            logInfo(TAG) { "Creating messaging socket..." }
            messagingSocket = socketProvider.openSocket()
            reader = socket.getInputStream().bufferedReader()
            writer = socket.getOutputStream().bufferedWriter()
            logInfo(TAG) { "Messaging socket created!" }
        }
    }

    private suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            messagingSocket?.close()
            messagingSocket = null
        }
    }

    companion object {
        private const val TAG = "Messenger"
    }
}
