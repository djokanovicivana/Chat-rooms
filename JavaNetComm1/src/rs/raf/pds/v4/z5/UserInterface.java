import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class UserInterface extends Application {

    private ChatClient chatClient;

    @Override
    public void start(Stage primaryStage) {
        TextField usernameField = new TextField();
        Button connectButton = new Button("Connect");

        VBox root = new VBox(usernameField, connectButton);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simple Chat Client");
        primaryStage.show();

        connectButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                startChat(username);
                primaryStage.close();
            }
        });
    }

    private void startChat(String username) {
        Stage chatStage = new Stage();

        TextArea chatArea = new TextArea();
        TextField inputField = new TextField();
        Button sendButton = new Button("Send");

        VBox chatRoot = new VBox(chatArea, inputField, sendButton);

        chatClient = new ChatClient("localhost", 9000, username);

        sendButton.setOnAction(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                chatClient.handleUserInput(message);
                inputField.clear();
            }
        });

        try {
            chatClient.start();
            chatClient.setMessageListener(message -> {
                handle(message, chatArea);
            });
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error connecting to the server: " + ex.getMessage());
        }

        Scene chatScene = new Scene(chatRoot, 400, 300);
        chatStage.setScene(chatScene);
        chatStage.setTitle("Chat Client - " + username);
        chatStage.show();
    }

    private void handle(String message, TextArea chatArea) {
        if (message.startsWith("/PRIVATE")) {
            handlePrivateMessage(message, chatArea);
        } else if (message.startsWith("/INFO")) {
            handleInfoMessage(message, chatArea);
        } else if (message.startsWith("/ROOM")) {
            handleRoomMessage(message, chatArea);
        } else {
            // Handle other types of messages
            chatArea.appendText(message + "\n");
        }
    }

    private void handlePrivateMessage(String message, TextArea chatArea) {
        // Implement handling for private messages
        // Format: /PRIVATE @recipient_username @message
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String recipient = parts[1];
            String privateMessage = parts[2];
            chatArea.appendText("Private message to " + recipient + ": " + privateMessage + "\n");
        }
    }

    private void handleInfoMessage(String message, TextArea chatArea) {
        // Implement handling for info messages
        // Format: /INFO @info_message
        String[] parts = message.split(" ", 2);
        if (parts.length == 2) {
            String infoMessage = parts[1];
            chatArea.appendText("Info message: " + infoMessage + "\n");
        }
    }

    private void handleRoomMessage(String message, TextArea chatArea) {
        // Implement handling for room messages
        // Format: /ROOM @room_name
        String[] parts = message.split(" ", 2);
        if (parts.length == 2) {
            String roomName = parts[1];
            chatArea.appendText("You joined room: " + roomName + "\n");
        }
    }

    @Override
    public void stop() {
        if (chatClient != null) {
            chatClient.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
