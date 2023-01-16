package com.alibagherifam.kavoshgar

fun interface Logger {
    companion object {
        val Default = Logger { _, _ -> }
    }

    fun log(tag: String, message: String)
}
