package com.droidgpt.model;


import androidx.annotation.NonNull;

public class ChatMessage {

    private final ApiReply reply;
    private final boolean sent;
    private boolean code;
    private final long time;


    public ChatMessage(ApiReply reply, boolean sent, long time) {
        this.reply = reply;
        this.sent = sent;
        this.time = time;
    }

    public String getText() {
        return reply.getText();
    }

    public Boolean getError() {
        return reply.getError();
    }

    public boolean isSent() {
        return sent;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMessage{" +
                "text='" + reply.getText() + '\'' +
                ", sent=" + sent +
                '}';
    }

    public void setCode(boolean code) {
        this.code = code;
    }

    public boolean isCode() {
        return code;
    }

    public long getTime() {
        return time;
    }

    public ApiReply getReply() {
        return reply;
    }
}
