package rs.raf.pds.v4.z5;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class UI extends Application {

    private int clientCount = 0;

    public void start(Stage primaryStage) {
        // Korisničko ime se unosi putem jednostavnog TextFilda
        TextField usernameField = new TextField("User" + (++clientCount));
        Button startButton = new Button("Start Chat");

        VBox root = new VBox(usernameField, startButton);

        startButton.setOnAction(e -> {
            String username = usernameField.getText().trim();

            if (!username.isEmpty()) {
                // Kreiranje ChatClient objekta sa odabranim korisničkim imenom
                ChatClient chatClient = new ChatClient("localhost", 9000, username);

                // Dodajte ostatak logike vezane za chatClient
                createNewClient(chatClient);

                // Dodajte ostatak logike za GUI
                Button createClientButton = new Button("Create New Client");
                VBox chatRoot = new VBox(createClientButton);

                createClientButton.setOnAction(event -> createNewClient(chatClient));

                Scene chatScene = new Scene(chatRoot, 300, 200);
                primaryStage.setScene(chatScene);
                primaryStage.setTitle("Chat Client Manager");
                primaryStage.show();
            }
        });

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Enter your username");
        primaryStage.show();
    }

    private void createNewClient(ChatClient chatClient) {
        Stage clientStage = new Stage();

        TextArea chatArea = new TextArea();
        TextField inputField = new TextField();
        Button sendButton = new Button("Send");

        VBox clientRoot = new VBox(chatArea, inputField, sendButton);

        //chatClient.setMessageListener(message -> {
            // Implementacija listenera za poruke od servera
            // Dodajte kod koji će dodavati poruke u chatArea
       // });

      
    }

    public static void main(String[] args) {
        launch(args);
    }
}
