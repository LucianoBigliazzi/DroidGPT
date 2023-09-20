package com.droidgpt.data.database

import com.droidgpt.model.MessageData
import java.time.LocalDate

sealed interface ConversationEvent {

    object SaveConversation                                                 : ConversationEvent
    data class SetCreationDate(val localDate: LocalDate)                    : ConversationEvent
    data class SetTitle(val title : String)                                 : ConversationEvent
    data class SetID(val id: Int)                                           : ConversationEvent
    data class SetMessageDataList(val messageDataList: List<MessageData>)   : ConversationEvent
    data class DeleteConversation(val conversation: Conversation)           : ConversationEvent
    data class SortConversations(val sortType: SortType)                    : ConversationEvent
}