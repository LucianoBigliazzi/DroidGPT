package com.droidgpt.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ConversationRepository (private val dao: ConversationDao) {

    val allConversations : Flow<List<Conversation>> = dao.getAllConversations()

    @WorkerThread
    suspend fun insert(conversation: Conversation) {
        dao.upsertConversation(conversation)
    }
}