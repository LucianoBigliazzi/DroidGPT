package com.droidgpt.model;

public class ApiReply {

    private final String text;
    private final Boolean error;

    public ApiReply(String text, Boolean error){
        this.text = text;
        this.error = error;
    }

    public String getText() {
        return text;
    }

    public Boolean getError() {
        return error;
    }
}
