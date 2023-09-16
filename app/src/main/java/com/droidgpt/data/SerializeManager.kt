package com.droidgpt.data

import com.aallam.openai.api.chat.ChatMessage
import com.droidgpt.model.MessageData
import com.droidgpt.model.TimeFormats
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDateTime


// Parse a list of MessageData object to Json
fun serializeList(messageDataList: MessageDataList) : String {

    val json = StringBuilder("{\"list\":[")

    for(item in messageDataList.list){
        json.append(serializeMessageData(item))
        if(item != messageDataList.list.last())
            json.append(",")
    }

    json.append("]}")

    return json.toString()
}


// Parse MessageData object to Json
fun serializeMessageData(messageData: MessageData) : String {

    val gson = Gson()
    val serializedMessage = StringBuilder("{\"messageData\":")

    serializedMessage.append(gson.toJson(messageData.chatMessage))

    serializedMessage.append(",\"time\":\"")
    serializedMessage.append(messageData.messageTime.format(TimeFormats.DATE_TIME))
    serializedMessage.append("\"}")


    return serializedMessage.toString()
}


fun deserializeList(jsonString: String): List<MessageData> {

    val gson = Gson()
    val list = gson.fromJson(jsonString, MessageDataList::class.java)

    return list.list
}

fun deserializeMessageData(jsonString: String): MessageData {

    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    val chatMessage: ChatMessage
    val time: LocalDateTime

    return gson.fromJson(jsonString, MessageData::class.java)
}


fun serializeConversationList(conversationsList: ConversationsList) : String {

    val jsonString = StringBuilder("{\"conversationList\":[")

    for(index in 0..conversationsList.conversationsList.size - 1){
        jsonString.append(serializeList(conversationsList.conversationsList[index]))
        if(index < conversationsList.conversationsList.size - 1)
            jsonString.append(",")
    }

    jsonString.append("]}")

    return jsonString.toString()
}

fun deserializeConversationList(jsonString: String) : ConversationsList {

    val gson = Gson()

    return gson.fromJson(jsonString, ConversationsList::class.java)
}