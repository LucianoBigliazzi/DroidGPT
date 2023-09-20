package com.droidgpt.data;

import com.droidgpt.model.ApiReply;

public interface Callback {

    void OnResponse(ApiReply response);
}
