package com.droidgpt.data;

import android.content.Context;

import org.ini4j.Ini;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class KeyManager {

    KeyManager(){}

    /**
     * Checks if a given key is valid, calling openai api
     * @param key key to check
     * @return true if key is valid, false otherwise
     * @throws InterruptedException
     */
    public static boolean validateKey(String key) throws InterruptedException {

        OkHttpClient okHttpClient = new OkHttpClient();
        AtomicInteger statusCode = new AtomicInteger();

        Thread thread = new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/engines")
                        .addHeader("Authorization", "Bearer " + key)
                        .get()
                        .build();

                Response response = okHttpClient.newCall(request).execute();

                statusCode.set(response.code());

                System.out.println("RESPONSE: " + response.isSuccessful());

            } catch (IOException e){
                e.printStackTrace();
            }
        });

        thread.start();
        thread.join();

        return statusCode.get() == 200;

    }

    public void saveKeyToIni(String key, Context context) throws IOException {

        InputStream inputStream = context.getAssets().open("key.ini");

        File file = new File(context.getFilesDir(), "key.ini");
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while((line = bufferedReader.readLine()) != null)
            stringBuilder.append(line).append("\n");

        bufferedReader.close();
        inputStream.close();

        Ini ini = new Ini(file);

        ini.put("OPENAI", "key", key);
        ini.store();
        //System.out.println("KEY now: " + ini.get("OPENAI", "key"));

    }
}
