package com.example.chatplugin.ui;

import com.example.chatplugin.api.ApiClient;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatWindow extends SimpleToolWindowPanel {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton analyzeButton;
    private final Project project;

    public ChatWindow(Project project) {
        super(true, true);
        this.project = project;

        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");
        analyzeButton = new JButton("Analyze Code");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        inputPanel.add(analyzeButton, BorderLayout.WEST);

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

        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = inputField.getText();
                if (!inputText.isEmpty()) {
                    analyzeSelectedCodeAndModify(inputText);
                } else {
                    chatArea.append("Please enter a command to analyze the code.\n");
                }
            }
        });
    }

    private void sendMessage(String message) {
        chatArea.append("You: " + message + "\n");

        // Обращаемся к ChatGPT через ApiClient и получаем ответ
        String response = ApiClient.getInstance().getChatGptResponse(message);

        // Проверяем, если в ответе содержатся инструкции по модификации кода
        if (response.startsWith("MODIFY_CODE:")) {
            String modificationInstructions = response.replace("MODIFY_CODE:", "");
            applyCodeModification(modificationInstructions);
        } else {
            chatArea.append("ChatGPT: " + response + "\n");
        }
    }

    private void analyzeSelectedCodeAndModify(String userRequest) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            String selectedText = editor.getSelectionModel().getSelectedText();
            if (selectedText != null) {
                String prompt = "Modify the following code based on the request: '" + userRequest + "'\n" + selectedText;
                String response = ApiClient.getInstance().getChatGptResponse(prompt);

                if (response.startsWith("MODIFY_CODE:")) {
                    String modificationInstructions = response.replace("MODIFY_CODE:", "");
                    applyCodeModification(modificationInstructions);
                } else {
                    chatArea.append("ChatGPT: " + response + "\n");
                }
            } else {
                chatArea.append("No code selected. Please select code or open the desired file.\n");
            }
        }
    }

    private void applyCodeModification(String modificationInstructions) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
                Document document = editor.getDocument();
                int startOffset = editor.getSelectionModel().getSelectionStart();
                int endOffset = editor.getSelectionModel().getSelectionEnd();

                // Применение изменений: например, заменяем выделенный текст на новый
                document.replaceString(startOffset, endOffset, modificationInstructions);

                chatArea.append("Applied modifications to the code.\n");
            } else {
                chatArea.append("Error: Could not apply modifications, no active editor found.\n");
            }
        });
    }
}
