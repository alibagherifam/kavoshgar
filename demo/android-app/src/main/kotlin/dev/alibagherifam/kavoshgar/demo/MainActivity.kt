package dev.alibagherifam.kavoshgar.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme
import dev.alibagherifam.kavoshgar.logger.Log
import dev.alibagherifam.kavoshgar.logger.LogPriority
import dev.alibagherifam.kavoshgar.logger.Logger
import android.util.Log as AndroidLog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.install(getLogger())
        setContent {
            AppTheme {
                NavHost()
            }
        }
    }
}

private fun getLogger() = Logger { priority, tag, message, error ->
    when (priority) {
        LogPriority.INFO -> AndroidLog.i(tag, message!!)
        LogPriority.ERROR -> AndroidLog.e(tag, message, error)
    }
}
