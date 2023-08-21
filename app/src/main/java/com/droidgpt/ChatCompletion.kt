package com.droidgpt

import android.content.Context
import android.content.SharedPreferences
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds

var openAI : OpenAI = OpenAI(
    token = "your_key"
)


fun init(context: Context){

    openAI = OpenAI(
        token = getKey(context),
        timeout = Timeout(socket = 120.seconds)
    )
}



@OptIn(BetaOpenAI::class)
suspend fun completion(text : String) : String {

    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.User,
                content = text
            )
        )
    )

    val reply = openAI.chatCompletion(chatCompletionRequest).choices[0].message?.content

    return reply.toString()
}


fun getKey(context: Context) : String{

    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    println("Persistent sharedPreferences: " + sharedPreferences.getString("api_key", "null"))
    return sharedPreferences.getString("api_key", "null")!!
}