package com.droidgpt.data.database

import com.droidgpt.model.MessageData
import java.time.LocalDate

data class ConversationState(
    val conversations: List<Conversation> = emptyList(),
    val creationDate: LocalDate = LocalDate.now(),
    val title: String = "",
    //val id: Int = 0,
    val messageDataList: List<MessageData> = emptyList(),
    val sortType: SortType = SortType.ID
)