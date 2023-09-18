package com.droidgpt.data

import androidx.compose.animation.core.animateRectAsState
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.droidgpt.model.MessageData
import com.droidgpt.model.TimeFormats
import com.google.gson.Gson
import junit.framework.TestCase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SerializeManagerKtTest : TestCase() {

    private val time: LocalDateTime = LocalDateTime.of(2023, 9, 15, 14, 30);
    private val messageData1 = MessageData(ChatMessage(ChatRole.Assistant, content = "Ciao!"), time)
    private val messageData2 = MessageData(ChatMessage(ChatRole.User, content = "Ciao a te!"), time)
    private val messageData3 = MessageData(ChatMessage(ChatRole.Assistant, content = "Ciao ancora"), time)

    private val messageDataList = MessageDataList(listOf(messageData1, messageData2, messageData3))

    private val list = listOf(messageData1, messageData2, messageData3)
    private val conversation = Conversation(time.toLocalDate(),"title",1, list)
    private val conversationsList = ConversationsList(listOf(conversation, conversation))


    fun testSerializeMessageData() {

        val serializedMessageData = serializeMessageData(messageData1)

        println(serializedMessageData)

        assertEquals(
            "{\"messageData\":{\"role\":\"assistant\",\"content\":\"Ciao!\"},\"time\":\"15/09/2023 14:30\"}",
            serializedMessageData
        )
    }

    fun testDeserializeMessageData() {

        val messageData = deserializeMessageData(serializeMessageData(messageData = messageData1))

        println(messageData)

        println(messageData.chatMessage.content)
        println(messageData.messageTime.dayOfMonth)

        assertEquals(messageData1.chatMessage.content, messageData.chatMessage.content)
    }

    fun testSerializeConversation() {

        println(serializeConversation(conversation = conversation))

        println(time.toLocalDate())

        assertEquals(
            "{\"creationDate\":\"15/09/2023\",\"title\":\" title\",\"id\":1,\"list\":[{\"messageData\":{\"role\":\"assistant\",\"content\":\"Ciao!\"},\"time\":\"15/09/2023 14:30\"},{\"messageData\":{\"role\":\"user\",\"content\":\"Ciao a te!\"},\"time\":\"15/09/2023 14:30\"},{\"messageData\":{\"role\":\"assistant\",\"content\":\"Ciao ancora\"},\"time\":\"15/09/2023 14:30\"}]}",
            serializeConversation(conversation = conversation)
        )

    }

    fun testDeserializeConversation() {

        val conversation = deserializeConversation(serializeConversation(conversation = conversation))

        println("CREATION DATE: " + conversation.creationDate.format(TimeFormats.DATE))

        println("ID: " + conversation.id)
        println("TITLE: " + conversation.title)
        conversation.messagesList.forEach { println(it.chatMessage.toString() + "  " + it.messageTime) }
    }

    fun testSerializeConversationList() {

        println(serializeConversationList(conversationsList = conversationsList))
    }

    fun testDeserializeConversationList() {

        val conversationsListString = serializeConversationList(conversationsList = conversationsList)

        val conversationsList = deserializeConversationList(conversationsListString)

        println(conversationsList)
        println(conversationsList.conversationsList[0].messagesList[0].messageTime.dayOfMonth)
        println(conversationsList.conversationsList[0].messagesList[0].chatMessage.content)
    }

    fun testSerializeMessageDataList() {

        println(serializeMessageDataList(messageDataList = list))
    }
    fun testDeserializeMessageDataList() {

        println(deserializeMessageDataList(serializeMessageDataList(list)).messagesList[0].chatMessage)
        println(deserializeMessageDataList(serializeMessageDataList(list)).messagesList[0].messageTime)
    }
}