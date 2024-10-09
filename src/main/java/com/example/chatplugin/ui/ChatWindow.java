package com.example.chatplugin.ui;

import com.example.chatplugin.api.ApiClient;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFileManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatWindow extends SimpleToolWindowPanel {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JComboBox<String> chatList;  // Список чатов
    private JButton createChatButton;    // Кнопка создания нового чата
    private final Project project;
    private boolean initialCodeSent = false;

    public ChatWindow(Project project) {
        super(true, true);
        this.project = project;

        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Панель для работы с чатами
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatList = new JComboBox<>();
        createChatButton = new JButton("Create Chat");

        chatPanel.add(chatList, BorderLayout.CENTER);
        chatPanel.add(createChatButton, BorderLayout.EAST);

        add(chatPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = inputField.getText();
                if (!inputText.isEmpty()) {
                    sendMessage(inputText);
                    inputField.setText("");
                }
            }
        });

        // Загрузка списка чатов
        loadChatList();

        // Создание нового чата
        createChatButton.addActionListener(e -> {
            String newChatName = JOptionPane.showInputDialog("Enter new chat name:");
            if (newChatName != null && !newChatName.isEmpty()) {
                createNewChat(newChatName);
            }
        });

        // Отправляем полный код проекта при первом запуске
        if (!initialCodeSent) {
            sendInitialCode();
        }

        // Добавляем слушатель изменений файлов
        ApplicationManager.getApplication().invokeLater(() -> {
            VirtualFileManager.getInstance().addVirtualFileListener(new com.example.chatplugin.FileChangeListener(project.getName()));
        });
    }

    private void loadChatList() {
        ApiClient.getInstance().getChatApiService().getChats().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (String chat : response.body()) {
                        chatList.addItem(chat);
                    }
                } else {
                    chatArea.append("Failed to load chat list: " + response.message() + "\n");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                chatArea.append("Error loading chats: " + t.getMessage() + "\n");
                t.printStackTrace();
            }
        });
    }


    private void createNewChat(String chatName) {
        ApiClient.getInstance().getChatApiService().createChat(chatName).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    chatList.addItem(chatName);
                    chatArea.append("Chat '" + chatName + "' created successfully.\n");
                } else {
                    chatArea.append("Failed to create chat.\n");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                chatArea.append("Error creating chat: " + t.getMessage() + "\n");
            }
        });
    }

    private void sendInitialCode() {
        try {
            String projectPath = project.getBasePath();
            String fullCode = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(projectPath)));

            ApiClient.getInstance().getChatApiService().sendInitialCode(project.getName(), fullCode)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            chatArea.append("Initial code sent successfully.\n");
                            initialCodeSent = true;
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            chatArea.append("Error sending initial code: " + t.getMessage() + "\n");
                        }
                    });
        } catch (Exception e) {
            chatArea.append("Failed to read project code: " + e.getMessage() + "\n");
        }
    }

    private void sendMessage(String message) {
        chatArea.append("You: " + message + "\n");

        // Здесь будет логика отправки сообщений
        String response = "Response from ChatGPT";
        chatArea.append("ChatGPT: " + response + "\n");
    }
    private String readAllFiles(File dir) throws Exception {
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("Directory does not exist: " + dir.getAbsolutePath());
        }

        try (Stream<File> files = Files.walk(dir.toPath()).map(java.nio.file.Path::toFile)) {
            return files.filter(File::isFile)
                    .map(file -> {
                        try {
                            return new String(Files.readAllBytes(file.toPath()));
                        } catch (Exception e) {
                            chatArea.append("Failed to read file: " + file.getName() + "\n");
                            return "";
                        }
                    })
                    .collect(Collectors.joining("\n"));
        }
    }

}
