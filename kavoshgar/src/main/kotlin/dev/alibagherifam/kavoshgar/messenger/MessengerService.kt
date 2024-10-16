package dev.alibagherifam.kavoshgar.messenger

import dev.alibagherifam.kavoshgar.Constants
import dev.alibagherifam.kavoshgar.messenger.MessagingSocketState.Connected
import dev.alibagherifam.kavoshgar.messenger.MessagingSocketState.Disconnected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import de.halfbit.logger.i as logInfo

class MessengerService(private val socketProvider: SocketProvider) {
    private var state: MessagingSocketState = Disconnected

    suspend fun sendMessage(message: String) {
        val state = state
        check(state is Connected) { "Socket is not connected!" }
        logInfo(TAG) { "Sending message..." }
        withContext(Dispatchers.IO) {
            state.writer.run {
                write(message)
                newLine()
                flush()
            }
        }
        logInfo(TAG) { "Message sent!" }
    }

    /* TODO: Change isConnected signaling mechanism (Perhaps
        we should move [KavoshgarServer] into [MessagingService])
    * */
    fun receiveMessages(): Flow<String> =
        flow {
            connect()
            emit("")
            while (true) {
                readMessage()?.let { emit(it) }
                delay(Constants.MESSAGING_INTERVALS)
            }
        }.onCompletion { disconnect() }

    private suspend fun readMessage(): String? {
        val state = state
        check(state is Connected) { "Socket is not connected!" }
        logInfo(TAG) { "Receiving message..." }
        return withContext(Dispatchers.IO) {
            state.reader.readLine()
        }.also {
            logInfo(TAG) { "Message received!" }
        }
    }

    private suspend fun connect() {
        check(state is Disconnected) { "Socket is already connected!" }
        logInfo(TAG) { "Creating messaging socket..." }
        state = withContext(Dispatchers.IO) {
            Connected(socketProvider.openSocket())
        }
        logInfo(TAG) { "Messaging socket created!" }
    }

    private suspend fun disconnect() {
        val currentState = state
        check(currentState is Connected) { "Socket is not connected!" }
        withContext(Dispatchers.IO) {
            currentState.socket.close()
        }
        state = Disconnected
    }

    companion object {
        private const val TAG = "Messenger"
    }
}
