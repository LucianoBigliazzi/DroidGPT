package com.droidgpt.model;

import com.aallam.openai.api.chat.ChatMessage;
import com.droidgpt.data.LocalDateTimeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageData implements Serializable {

    @SerializedName("messageData")
    private ChatMessage chatMessage;

    @SerializedName("time")
    @JsonAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime localDateTime;


    public MessageData(ChatMessage chatMessage, LocalDateTime localDateTime){
        this.chatMessage = chatMessage;
        this.localDateTime = localDateTime;
    }


    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public LocalDateTime getMessageTime() {
        return localDateTime;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public void setMessageTime(LocalDateTime messageTime) {
        this.localDateTime = messageTime;
    }

    public void append(String input){
    }
}
