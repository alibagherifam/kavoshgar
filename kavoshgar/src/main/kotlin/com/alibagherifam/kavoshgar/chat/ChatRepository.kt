package com.alibagherifam.kavoshgar.chat

import com.alibagherifam.kavoshgar.Constants
import com.alibagherifam.kavoshgar.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.Socket

class ChatRepository(
    private val socketProvider: ChatSocketProvider,
    private val logger: Logger = Logger.Default
) {
    private var socket: Socket? = null
    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter

    suspend fun sendMessage(message: String) {
        withContext(Dispatchers.IO) {
            logger.log(tag = "Chat", message = "Chat: Sending message...")
            writer.write(message)
            writer.newLine()
            writer.flush()
            logger.log(tag = "Chat", message = "Chat: Message sent!")
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
                logger.log(tag = "Chat", message = "Chat: Receiving message...")
                reader.readLine()?.let { emit(it) }
                logger.log(tag = "Chat", message = "Chat: Message received!")
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
        logger.log(tag = "Chat", message = "Chat: Creating chat socket...")
        socket = socketProvider.openSocket()
        reader = socket!!.getInputStream().bufferedReader()
        writer = socket!!.getOutputStream().bufferedWriter()
        logger.log(tag = "Chat", message = "Chat: Chat socket created!")
    }

    private fun disconnect() {
        socket?.close()
        socket = null
    }
}
