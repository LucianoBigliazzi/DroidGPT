package com.droidgpt.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.droidgpt.data.labels.SettingsLabels;
import com.droidgpt.model.ApiReply;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Data implements Callback {

    public final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
            .build();

    public final String conversationFileName = "conversation.json";
    private final Context context;
    private FileInputStream fileInputStream;
    private InputStreamReader inputStreamReader;
    private MediaType mediaType;

    public Data(Context context){
        this.context = context;

        try {
            initializeJson();

            this.fileInputStream = context.openFileInput(conversationFileName);
            this.inputStreamReader = new InputStreamReader(fileInputStream);
            this.mediaType = MediaType.parse("application/json");

        } catch (Exception e){
            e.printStackTrace();
        }


    }


    public void deleteJson(Context context, String fileName){

        File file = new File(context.getFilesDir().getAbsolutePath() + File.separator + fileName);

        if(file.exists())
            if(file.delete())
                System.out.println("JSON deleted");
            else
                System.err.println("Failed to delete JSON");

    }


    public void createJson(Context context, String filename) throws IOException {

        String jsonString = "{\"model\":\"" + "gpt-3.5-turbo" + "\",\"messages\":[" +
                "{\"role\": \"system\", \"content\":\"" + getBehaviourFromSharedPreferences() + "\"}" +
                "],\"temperature\":" + getFloatFromSharedPreferences(SettingsLabels.SETTINGS, SettingsLabels.TEMPERATURE) + "," +
                "\"stream\": false}";

        System.out.println("New JSON: " + jsonString);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        FileWriter fileWriter = new FileWriter(context.getFilesDir().getAbsolutePath() + File.separator + filename);
        fileWriter.write(jsonObject.toString());
        fileWriter.flush();
        fileWriter.close();


    }

    private String getEngineFromSharedPreferences() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return sharedPreferences.getString("engine", "gpt-3.5-turbo");
    }


    public String resolveKeyShared(){

        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsLabels.SETTINGS, Context.MODE_PRIVATE);
        System.out.println("Persistent sharedPreferences: " + sharedPreferences.getString(SettingsLabels.API_KEY, "null"));
        return sharedPreferences.getString(SettingsLabels.API_KEY, "null");
    }

    public ApiReply experimentalInterrogateAPI(String questionText, boolean isTitleRequest, Callback callback) throws FileNotFoundException {

        System.out.println("Question text: " + questionText);

        ApiReply apiReply;

        //      ADD INPUT TO JSON FILE
        addQuestion(context, questionText, conversationFileName);


        String key = resolveKeyShared();



        RequestBody body = RequestBody.create(getJsonString(context), mediaType);


        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + key) // Sostituisci con la tua chiave API
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();

            // Parsing della risposta JSON
//            JsonArray jsonArray = new JsonArray();  STREAM
//            jsonArray.add(responseData);
//            System.out.println(jsonArray);
            JSONObject jsonObject = new JSONObject(responseData);
            System.out.println(jsonObject);
            String answer;

            if(jsonObject.has("choices")) {
                answer = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

                apiReply = new ApiReply(answer, false);


                callback.OnResponse(apiReply);

                System.out.println("ANSWER: " + answer);
                System.out.println(getJsonString(context));

                //addAnswer(context, answer);     // Saves the reply, it should be put after printing it in the UI

                response.close();

            } else if (jsonObject.has("error")) {
                answer = jsonObject.getJSONObject("error").getString("message");
                apiReply = new ApiReply(answer, true);
                callback.OnResponse(apiReply);
            } else {
                apiReply = new ApiReply("ERROR", true);
                callback.OnResponse(apiReply);
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("API CALL EXECUTED");

        return apiReply;
    }

    public String getJsonString(Context context) throws FileNotFoundException {

        File file = new File(context.getFilesDir(), conversationFileName);

        JsonElement jsonElement = JsonParser.parseReader(new FileReader(file));

        return jsonElement.toString();

    }

    public void addQuestion(Context context, String question, String filename){

        try {

            fileInputStream = context.openFileInput(filename);
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
//            StringBuilder stringBuilder = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null)
//                stringBuilder.append(line);
//
//            bufferedReader.close();
//
//            String jsonString = stringBuilder.toString();



            //JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(fileInputStream)).getAsJsonObject();   // REPLACES THE COMMENTED CODE UP HERE
            JsonArray jsonArray = jsonObject.getAsJsonArray("messages");

            JsonObject userMessage = new JsonObject();
            userMessage.addProperty("role", "user");
            userMessage.addProperty("content", question);

            jsonArray.add(userMessage);

            Gson gson = new GsonBuilder().create();
            String updatedJson = gson.toJson(jsonObject);

            FileOutputStream fileOutputStream = context.openFileOutput("conversation.json", Context.MODE_PRIVATE);
            fileOutputStream.write(updatedJson.getBytes());
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeTitleQuestionFromJson(Context context, int last) {

        try {
            fileInputStream = context.openFileInput("conversation.json");

            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(fileInputStream)).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("messages");

            jsonArray.remove(last);

            Gson gson = new GsonBuilder().create();
            String updatedJson = gson.toJson(jsonObject);

            FileOutputStream fileOutputStream = context.openFileOutput("conversation.json", Context.MODE_PRIVATE);
            fileOutputStream.write(updatedJson.getBytes());
            fileOutputStream.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void addAnswer(Context context, String answer){

        File file = new File(context.getFilesDir(), "conversation.json");

        try {
            JsonObject jsonObject = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("messages");
            JsonObject aiMessage = new JsonObject();

            aiMessage.addProperty("role", "assistant");
            aiMessage.addProperty("content", answer);

            jsonArray.add(aiMessage);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String updatedJson = gson.toJson(jsonObject);

            FileOutputStream fileOutputStream = context.openFileOutput("conversation.json", Context.MODE_PRIVATE);
            fileOutputStream.write(updatedJson.getBytes());
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnResponse(ApiReply reply) {
    }

    public void initializeJson() throws IOException {

        deleteJson(context, conversationFileName);
        createJson(context, conversationFileName);

        //this.fileInputStream = context.openFileInput(conversationFileName);

    }


    public void saveStringToSharedPreferences(String src, String key, String value){

        SharedPreferences sharedPreferences = context.getSharedPreferences(src, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getFromSharedPreferences(String src, String key){

        SharedPreferences sharedPreferences = context.getSharedPreferences(src, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "null");
    }

    public String getBehaviourFromSharedPreferences(){

        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsLabels.SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SettingsLabels.BEHAVIOUR, "You are a helpful assistant");
    }

    public void saveFloatToSharedPreferences(String src, String key, Float num){

        SharedPreferences sharedPreferences = context.getSharedPreferences(src, Context.MODE_PRIVATE);
        sharedPreferences.edit().putFloat(key, num).apply();
    }

    public Float getFloatFromSharedPreferences(String src, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(src, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, 0.7F);
    }

    public void saveBooleanToSharedPreferences(String src, String key, Boolean check){

        SharedPreferences sharedPreferences = context.getSharedPreferences(src, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, check).apply();
    }

    public Boolean getBooleanFromSharedPreferences(String src, String key, Boolean _default){
        SharedPreferences sharedPreferences = context.getSharedPreferences(src, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, _default);
    }

}
