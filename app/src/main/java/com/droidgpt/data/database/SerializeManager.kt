package com.droidgpt.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aallam.openai.api.chat.ChatMessage
import com.droidgpt.model.MessageData
import com.droidgpt.data.TimeFormats
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime

// Full serializable conversations class
data class ConversationsList(
    @SerializedName("conversationList")
    val conversationsList: List<Conversation>
)

// Serializable class for a single Conversation
@Entity
data class Conversation(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id : Int = 0,

    @JsonAdapter(LocalDateAdapter::class)
    @SerializedName("creationDate")
    val creationDate: LocalDate,

    @SerializedName("title")
    val title: String,

    @SerializedName("list")
    val messagesList: List<MessageData>
)

data class MessageDataList(
    @SerializedName("list")
    val messagesList: List<MessageData>
)

// Class used to update titles
data class ConversationUpdate(
    val id: Int,
    val title: String
)


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

fun deserializeMessageData(jsonString: String): MessageData {

    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    val chatMessage: ChatMessage
    val time: LocalDateTime

    return gson.fromJson(jsonString, MessageData::class.java)
}


fun serializeMessageDataList(messageDataList: List<MessageData>) : String {

    val json = StringBuilder("{\"list\":[")

    for(item in messageDataList){
        json.append(serializeMessageData(item))
        if(item != messageDataList.last())
            json.append(",")
    }

    json.append("]}")

    return json.toString()
}

fun deserializeMessageDataList(jsonString: String) : MessageDataList {

    val gson = Gson()

    return gson.fromJson(jsonString, MessageDataList::class.java)
}


// Parse a list of MessageData object to Json
fun serializeConversation(conversation: Conversation) : String {

    val json = StringBuilder("{\"creationDate\":\""
            + conversation.creationDate.format(TimeFormats.DATE)
            + "\",\"title\":\" "
            + conversation.title
            + "\",\"id\":"
            + conversation.id
            + ","
            + "\"list\":["
    )

    for(item in conversation.messagesList){
        json.append(serializeMessageData(item))
        if(item != conversation.messagesList.last())
            json.append(",")
    }

    json.append("]}")

    return json.toString()
}


fun deserializeConversation(jsonString: String): Conversation {

    val gson = Gson()
    return gson.fromJson(jsonString, Conversation::class.java)
}


fun serializeConversationList(conversationsList: ConversationsList) : String {

    val jsonString = StringBuilder("{\"conversationList\":[")

    for(index in 0..conversationsList.conversationsList.size - 1){
        jsonString.append(serializeConversation(
            Conversation(
                creationDate = conversationsList.conversationsList[index].creationDate,
                title = conversationsList.conversationsList[index].title,
                id = conversationsList.conversationsList[index].id,
                messagesList = conversationsList.conversationsList[index].messagesList)
        ))
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