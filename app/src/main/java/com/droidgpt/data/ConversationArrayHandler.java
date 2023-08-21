package com.droidgpt.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class ConversationArrayHandler {

    private ArrayList<String> conversationArray = new ArrayList<>();
    private SharedPreferences sharedPrefs;



    public void addToSharedPreferences(Context context, String jsonString){

        conversationArray.add(jsonString);

        sharedPrefs = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        sharedPrefs.edit().putString("conversation_data_queue", conversationArray.toString()).apply();
    }


    public ArrayList<String> getConversationArray() {
        return conversationArray;
    }
}
