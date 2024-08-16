package dev.alibagherifam.kavoshgar.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme

fun main() = application {
    Window(
        title = "Kavoshgar Demo",
        onCloseRequest = ::exitApplication
    ) {
        AppTheme {
            NavHost()
        }
    }
}
