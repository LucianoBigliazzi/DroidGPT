package com.droidgpt.model;

public class TextMessage {

    private String text;
    private int code;

    public TextMessage(String text, int code){
        this.text = text;
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public int getCode() {
        return code;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
