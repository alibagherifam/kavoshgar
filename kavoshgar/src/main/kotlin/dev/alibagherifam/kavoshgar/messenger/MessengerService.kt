package dev.alibagherifam.kavoshgar.messenger

import dev.alibagherifam.kavoshgar.Constants
import dev.alibagherifam.kavoshgar.logger.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.Socket

class MessengerService(private val socketProvider: SocketProvider) {
    companion object {
        private const val TAG = "Messenger"
    }

    private var messagingSocket: Socket? = null
    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter

    suspend fun sendMessage(message: String) {
        withContext(Dispatchers.IO) {
            Log.i(TAG, message = "Sending message...")
            writer.run {
                write(message)
                newLine()
                flush()
            }
            Log.i(TAG, message = "Message sent!")
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
            Log.i(TAG, message = "Receiving message...")
            val message = reader.readLine()
            Log.i(TAG, message = "Message received!")
            message
        }
    }

    private suspend fun connect() {
        val socket = messagingSocket ?: return
        withContext(Dispatchers.IO) {
            Log.i(TAG, message = "Creating messaging socket...")
            messagingSocket = socketProvider.openSocket()
            reader = socket.getInputStream().bufferedReader()
            writer = socket.getOutputStream().bufferedWriter()
            Log.i(TAG, message = "Messaging socket created!")
        }
    }

    private suspend fun disconnect() {
        withContext(Dispatchers.IO) {
            messagingSocket?.close()
            messagingSocket = null
        }
    }
}
