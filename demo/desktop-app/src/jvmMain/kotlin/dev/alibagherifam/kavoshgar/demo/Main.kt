package dev.alibagherifam.kavoshgar.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme
import dev.alibagherifam.kavoshgar.logger.Log
import dev.alibagherifam.kavoshgar.logger.LogPriority
import dev.alibagherifam.kavoshgar.logger.Logger

fun main() = application {
    Log.install(getLogger())
    Window(
        title = "Kavoshgar Demo",
        onCloseRequest = ::exitApplication
    ) {
        AppTheme {
            NavHost()
        }
    }
}

private fun getLogger() = Logger { priority, tag, message, error ->
    val formattedMessage = when (priority) {
        LogPriority.INFO -> message!!
        LogPriority.ERROR -> error!!.let { it.message + it.stackTraceToString() }
    }
    println("${System.currentTimeMillis()}   $tag: $formattedMessage")
}
