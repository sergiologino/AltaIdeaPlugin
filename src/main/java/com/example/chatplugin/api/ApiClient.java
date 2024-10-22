package com.example.chatplugin.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {

    private static ApiClient instance;
    private final ChatGptService chatGptService;

    // OpenAI API URL
    private static final String CHAT_GPT_URL = "https://api.openai.com/";

    private ApiClient() {
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CHAT_GPT_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        chatGptService = retrofit.create(ChatGptService.class);
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    // Метод для отправки запроса к ChatGPT и получения ответа
    public String getChatGptResponse(String prompt) {
        try {
            // Формируем тело запроса
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo"); // Используйте актуальную модель
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            requestBody.put("max_tokens", 150);
            requestBody.put("temperature", 0.7);

            // Выполняем запрос к ChatGPT через Retrofit
            Call<Map<String, Object>> call = chatGptService.getCompletion("Bearer YOUR_API_KEY_HERE", requestBody);

            // Получаем ответ
            Map<String, Object> response = call.execute().body();
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null && message.containsKey("content")) {
                        return message.get("content").toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error interacting with ChatGPT.";
    }

    // Интерфейс для Retrofit, описывающий взаимодействие с OpenAI API
    public interface ChatGptService {
        @Headers("Content-Type: application/json")
        @POST("v1/chat/completions")
        Call<Map<String, Object>> getCompletion(@Header("Authorization") String authHeader, @Body Map<String, Object> requestBody);
    }
}
