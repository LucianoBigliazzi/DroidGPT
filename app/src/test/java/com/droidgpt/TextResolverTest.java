package com.droidgpt;

import com.droidgpt.data.TextResolver;
import com.droidgpt.model.TextMessage;

import junit.framework.TestCase;

import java.util.ArrayList;

public class TextResolverTest extends TestCase {

    public void testResolveText() {

        String test1 = "Resulting `AnnotatedString`: \"Hello, world!\"";
        String test2 = "This is a test code: ```println(\"Hello world\")```\n code is ended";

        arrayToTextBlocks(TextResolver.resolveText(test2));

        assertEquals(TextResolver.resolveText(test1).size(), 3);
        assertEquals(TextResolver.resolveText(test2).size(), 3);



        //System.out.println(TextResolver.resolveText(test2));
        //arrayToTextBlocks(TextResolver.resolveText(test1));
        //arrayToTextBlocks(TextResolver.resolveText(test2));
    }

    private void arrayToTextBlocks(ArrayList<TextMessage> list){

        for(TextMessage item : list){
            System.out.println("item: " + item.isCode() + " " + item.getText());
        }
    }
}

