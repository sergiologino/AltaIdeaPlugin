package com.example.chatplugin;

import com.example.chatplugin.api.ApiClient;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileChangeListener extends VirtualFileAdapter {

    private final String projectId;

    public FileChangeListener(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public void contentsChanged(VirtualFileEvent event) {
        VirtualFile file = event.getFile();
        Document document = FileDocumentManager.getInstance().getDocument(file);

        if (document != null) {
            // Получаем измененный текст из документа
            String changes = document.getText();

            // Отправляем изменения в API
            sendCodeChanges(file, changes);
        }
    }

    private void sendCodeChanges(VirtualFile file, String changes) {
        ApiClient.getInstance().getChatApiService().sendCodeChanges(projectId, changes)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        System.out.println("Changes in file " + file.getName() + " sent successfully.");
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        System.out.println("Failed to send changes: " + t.getMessage());
                    }
                });
    }
}
