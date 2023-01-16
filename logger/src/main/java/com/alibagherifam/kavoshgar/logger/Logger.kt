package com.alibagherifam.kavoshgar.logger

enum class LogPriority {
    INFO,
    ERROR
}

fun interface Logger {
    fun log(priority: LogPriority, tag: String, message: String?, error: Throwable?)
}

object Log {
    private var installedLogger = Logger { _, _, _, _ -> }

    fun install(logger: Logger) {
        installedLogger = logger
    }

    fun i(tag: String, message: String) {
        installedLogger.log(LogPriority.INFO, tag, message, error = null)
    }

    fun e(tag: String, error: Throwable) {
        installedLogger.log(LogPriority.ERROR, tag, message = null, error)
    }
}
