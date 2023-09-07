package com.droidgpt.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.droidgpt.data.Data
import com.droidgpt.viewmodel.ChatViewModel

class ChatViewModelFactory(private val data: Data) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(data) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}