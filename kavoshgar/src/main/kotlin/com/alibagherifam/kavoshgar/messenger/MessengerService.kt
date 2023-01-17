package com.alibagherifam.kavoshgar.messenger

import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.logger.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.Socket

class MessengerService(private val socketProvider: SocketProvider) {
    companion object {
        private const val TAG = "Messenger"
    }

    private var socket: Socket? = null
    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter

    suspend fun sendMessage(message: String) {
        withContext(Dispatchers.IO) {
            Log.i(TAG, message = "Sending message...")
            writer.write(message)
            writer.newLine()
            writer.flush()
            Log.i(TAG, message = "Message sent!")
        }
    }

    /* TODO: Change isConnected signaling mechanism (Perhaps
        we should move DiscoveryReplyRepository into ChatRepository)
    * */
    fun receiveMessages(): Flow<String> = flow {
        try {
            connect()
            emit("")
            while (true) {
                Log.i(TAG, message = "Receiving message...")
                reader.readLine()?.let { emit(it) }
                Log.i(TAG, message = "Message received!")
                delay(Constants.MESSAGING_INTERVALS)
            }
        } finally {
            disconnect()
        }
    }.flowOn(Dispatchers.IO)

    private fun connect() {
        if (socket != null) {
            return
        }
        Log.i(TAG, message = "Creating chat socket...")
        socket = socketProvider.openSocket()
        reader = socket!!.getInputStream().bufferedReader()
        writer = socket!!.getOutputStream().bufferedWriter()
        Log.i(TAG, message = "Chat socket created!")
    }

    private fun disconnect() {
        socket?.close()
        socket = null
    }
}
