package com.example.chatplugin.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static ApiClient instance;
    private final ChatApiService chatApiService;

    private ApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://your-api-url.com") // Здесь указываем базовый URL API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        chatApiService = retrofit.create(ChatApiService.class);
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public ChatApiService getChatApiService() {
        return chatApiService;
    }
}

