package dev.alibagherifam.kavoshgar.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.halfbit.logger.LoggableLevel
import de.halfbit.logger.initializeLogger
import de.halfbit.logger.sink.android.registerAndroidLogSink
import dev.alibagherifam.kavoshgar.demo.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initializeLogger {
            registerAndroidLogSink()
            loggableLevel = LoggableLevel.Everything
        }

        setContent {
            AppTheme {
                NavHost()
            }
        }
    }
}
