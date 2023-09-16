package com.droidgpt.data

import androidx.compose.animation.core.animateRectAsState
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.droidgpt.model.MessageData
import com.google.gson.Gson
import junit.framework.TestCase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SerializeManagerKtTest : TestCase() {

    private val time: LocalDateTime = LocalDateTime.of(2023, 9, 15, 14, 30);
    private val messageData1 = MessageData(ChatMessage(ChatRole.Assistant, content = "Ciao!"), time)
    private val messageData2 = MessageData(ChatMessage(ChatRole.User, content = "Ciao a te!"), time)
    private val messageData3 = MessageData(ChatMessage(ChatRole.Assistant, content = "Ciao ancora"), time)

    private val list: MessageDataList = MessageDataList(listOf(messageData1, messageData2, messageData3))
    private val conversationsList = ConversationsList(listOf(list, list))

    fun testSerializeList() {

        println(serializeList(messageDataList =  list))

        assertEquals(
            "{\"list\":[{\"messageData\":{\"role\":\"assistant\",\"content\":\"Ciao!\"},\"time\":\"15/09/2023 14:30\"},{\"messageData\":{\"role\":\"user\",\"content\":\"Ciao a te!\"},\"time\":\"15/09/2023 14:30\"},{\"messageData\":{\"role\":\"assistant\",\"content\":\"Ciao ancora\"},\"time\":\"15/09/2023 14:30\"}]}",
            serializeList(messageDataList = list)
        )

    }

    fun testSerializeMessageData() {

        val serializedMessageData = serializeMessageData(messageData1)

        println(serializedMessageData)

        assertEquals(
            "{\"messageData\":{\"role\":\"assistant\",\"content\":\"Ciao!\"},\"time\":\"15/09/2023 14:30\"}",
            serializedMessageData
        )
    }


    fun testDeserializeList() {

        val tmpList = deserializeList(serializeList(messageDataList = list))

        println(tmpList)
        tmpList.forEach { println(it.chatMessage.toString() + "  " + it.messageTime) }
    }


    fun testDeserializeMessageData() {

        val messageData = deserializeMessageData(serializeMessageData(messageData = messageData1))

        println(messageData)

        println(messageData.chatMessage.content)
        println(messageData.messageTime.dayOfMonth)

        assertEquals(messageData1.chatMessage.content, messageData.chatMessage.content)
    }

    fun testSerializeConversationList() {

        println(serializeConversationList(conversationsList = conversationsList))
    }

    fun testDeserializeConversationList() {

        val conversationsListString = serializeConversationList(conversationsList = conversationsList)

        val conversationsList = deserializeConversationList(conversationsListString)

        println(conversationsList)
        println(conversationsList.conversationsList[0].list[0].messageTime.dayOfMonth)
        println(conversationsList.conversationsList[0].list[0].chatMessage.content)
    }
}