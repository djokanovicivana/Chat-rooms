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
        primaryStage.setTitle("Chat aplikacija");
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
        Button sendButton = new Button("Posalji");

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
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("Error connecting to the server: " + ex.getMessage());
        }

        Scene chatScene = new Scene(chatRoot, 400, 300);
        chatStage.setScene(chatScene);
        chatStage.setTitle("Korisnik - " + username);
        chatStage.show();
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
