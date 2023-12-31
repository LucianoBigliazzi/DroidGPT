package com.droidgpt.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.droidgpt.data.database.ConversationDao
import com.droidgpt.data.Data
import com.droidgpt.viewmodel.ChatViewModel

class ChatViewModelFactory(private val data: Data, private val conversationDao: ConversationDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(data, conversationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}