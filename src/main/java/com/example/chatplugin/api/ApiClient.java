package com.example.chatplugin.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class ApiClient {

    private static ApiClient instance;
    private final ChatApiService chatApiService;

    // URL вашего API. Замените на актуальный URL
    private static final String BASE_URL = "https://your-api-url.com";

    private ApiClient() {
        // Настраиваем Gson с setLenient для обработки менее строгого JSON
        Gson gson = new GsonBuilder().setLenient().create();

        // Создаем экземпляр Retrofit с настроенным Gson-конвертером
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
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

    // Интерфейс для описания методов API
    public interface ChatApiService {
        @POST("/api/project/{projectId}/initial")
        Call<Void> sendInitialCode(@Path("projectId") String projectId, @Body String code);

        @POST("/api/project/{projectId}/changes")
        Call<Void> sendCodeChanges(@Path("projectId") String projectId, @Body String changes);

        @GET("/api/chats")
        Call<List<String>> getChats();

        @POST("/api/chats")
        Call<Void> createChat(@Body String chatName);
    }
}
