package dsa_lab_solutions.todoproject;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LoginDialog {
    private Stage dialogStage;
    private User loggedInUser;
    private Map<String, String> users;

    public LoginDialog(Stage owner) {
        users = loadUsers();
        dialogStage = new Stage();
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle("Login or Register");

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");
        Label infoLabel = new Label();

        HBox buttonBox = new HBox(10, loginButton, registerButton);
        VBox root = new VBox(10, userLabel, userField, passLabel, passField, buttonBox, infoLabel);
        root.setPadding(new Insets(20));


        loginButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText();
            if (users.containsKey(username) && users.get(username).equals(password)) {
                loggedInUser = new User(username, password);
                dialogStage.close();
            } else {
                infoLabel.setText("Incorrect credentials.");
            }
        });


        registerButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                infoLabel.setText("Username and password required.");
                return;
            }
            if (users.containsKey(username)) {
                infoLabel.setText("Username already exists.");
            } else {
                users.put(username, password);
                saveUsers();
                loggedInUser = new User(username, password);
                dialogStage.close();
            }
        });

        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
    }

    public User showAndWait() {
        dialogStage.showAndWait();
        return loggedInUser;
    }


    private Map<String, String> loadUsers() {
        Map<String, String> map = new HashMap<>();
        File file = new File("users.txt");
        if (!file.exists()) return map;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    private void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt"))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}