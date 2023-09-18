package com.droidgpt.data

import androidx.room.TypeConverter
import com.droidgpt.model.MessageData
import com.droidgpt.model.TimeFormats
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.time.LocalDate
import java.time.LocalDateTime

class ConversationTypeConverter {

    @TypeConverter
    fun messageDataConverter(messageData: MessageData) : String {
        return serializeMessageData(messageData = messageData)
    }

    @TypeConverter
    fun messageDataToJson(jsonString: String) : MessageData {
        return deserializeMessageData(jsonString = jsonString)
    }

    @TypeConverter
    fun messageDataListConverter(jsonString: String) : List<MessageData> {
        return deserializeMessageDataList(jsonString = jsonString).messagesList
    }

    @TypeConverter
    fun messageDataListToJson(list: List<MessageData>) : String {
        return serializeMessageDataList(messageDataList = list)
    }

    @TypeConverter
    fun creationDateToJson(dateTime: LocalDate) : String {
        val serializedDate = StringBuilder("\"creationDate\":\"")
        return serializedDate.append(dateTime.format(TimeFormats.DATE)).append("\"").toString()
    }

    @TypeConverter
    fun creationDateConverter(jsonString: String): LocalDate {

        println("Json STRING: $jsonString")

        val formedJsonString = "{$jsonString}"

        val jsonObject = JsonParser.parseString(formedJsonString).asJsonObject
        val creationDateString = jsonObject.get("creationDate").asString

        return LocalDate.parse(creationDateString, TimeFormats.DATE)
    }

    @TypeConverter
    fun conversationToJson(conversation: Conversation) : String {
        return serializeConversation(conversation = conversation)
    }

    @TypeConverter
    fun conversationConverter(jsonString: String) : Conversation {
        return deserializeConversation(jsonString = jsonString)
    }
}