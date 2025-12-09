package dev.alibagherifam.kavoshgar.demo.chat.model

import kotlin.time.Clock
import kotlin.time.Instant

data class Message(
    val isMine: Boolean,
    val content: String
) {
    val receiveInstant: Instant = Clock.System.now()
}

object FakeMessageFactory {
    fun create(): Message = createList().first()

    fun createList(): List<Message> =
        listOf(
            Message(
                isMine = true,
                content = "سلام! برای امتحان فردا چیزی خوندی؟"
            ),
            Message(
                isMine = false,
                content = "سلام. هنوز شروع نکردم 😃 احتمالا راحت باشه."
            ),
            Message(
                isMine = true,
                content = "منم چیزی نخوندم هنوز"
            )
        )
}
