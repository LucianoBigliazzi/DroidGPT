package com.droidgpt.data

import com.droidgpt.model.MessageData
import com.google.gson.annotations.SerializedName


data class MessageDataList(
    @SerializedName("list")
    val list: List<MessageData>
)

data class ConversationsList(
    @SerializedName("conversationList")
    val conversationsList: List<MessageDataList>
)
