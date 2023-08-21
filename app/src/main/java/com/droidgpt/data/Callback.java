package com.droidgpt.data;

import com.droidgpt.model.ApiReply;
import com.droidgpt.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public interface Callback {

    void OnResponse(ApiReply response);
}
