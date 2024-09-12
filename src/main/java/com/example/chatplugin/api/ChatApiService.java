package com.example.chatplugin.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ChatApiService {

    @POST("/api/project/{projectId}/initial")
    Call<Void> sendInitialCode(@Path("projectId") String projectId, @Body String code);

    @POST("/api/project/{projectId}/changes")
    Call<Void> sendCodeChanges(@Path("projectId") String projectId, @Body String changes);

    @GET("/api/chats")
    Call<List<String>> getChats();  // Метод для получения списка чатов

    @POST("/api/chats")
    Call<Void> createChat(@Body String chatName);  // Метод для создания нового чата
}
