package com.droidgpt.data;

import androidx.annotation.NonNull;
import androidx.compose.ui.text.AnnotatedString;

import com.droidgpt.model.ChatMessage;
import com.droidgpt.model.TextMessage;

import java.util.ArrayList;

public class TextResolver {


    /**
     * Differentiates between code parts and plain text parts, stores every part in a list of TextMessage
     * objects.
     * @param text the plain text to parse.
     * @return a list of TextMessage objects that contains the plain text and a boolean that tells if
     * it is a code part.
     */
    public static ArrayList<TextMessage> resolveText(String text){

        boolean singleQuote = false;
        boolean tripleQuote = false;
        ArrayList<TextMessage> textParts = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder().append("");


        for(int i = 0; i < text.length(); ){

            if(text.charAt(i) == '`'){


                if(text.charAt(i + 1) == '`' && !tripleQuote){
                    if(!tripleQuote && !singleQuote && stringBuilder.length() > 0){
                        textParts.add(new TextMessage(stringBuilder.toString(), 0));
                        stringBuilder.delete(0, stringBuilder.length());
                    }
                    tripleQuote = true;
                    i = i + 3;
                } else if (text.charAt(i + 1) == '`' && tripleQuote) {
                    textParts.add(new TextMessage(stringBuilder.toString(), 2));
                    stringBuilder.delete(0, stringBuilder.length());
                    tripleQuote = false;
                    i = i + 3;
                } else if (text.charAt(i + 1) != '`' && !singleQuote) {
                    if(!tripleQuote && !singleQuote && stringBuilder.length() > 0){
                        textParts.add(new TextMessage(stringBuilder.toString(), 0));
                        stringBuilder.delete(0, stringBuilder.length());
                    }
                    singleQuote = true;
                    i = i + 1;
                } else if (text.charAt(i + 1) != '`' && singleQuote) {
                    textParts.add(new TextMessage(stringBuilder.toString(), 1));
                    stringBuilder.delete(0, stringBuilder.length());
                    singleQuote = false;
                    i = i + 1;
                }
            } else {
                stringBuilder.append(text.charAt(i));
                i++;
            }
        }

        textParts.add(new TextMessage(stringBuilder.toString(), 0));

        return textParts;
    }


}
