package com.droidgpt.data

data class Message(val role: String, val content: String)

data class Conversation(val model: String, val messages: List<Message>, val temperature: Double, val stream: Boolean)
