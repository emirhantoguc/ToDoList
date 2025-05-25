package dsa_lab_solutions.todoproject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private User currentUser;
    private ListView<Task> todoList;

    private String getTaskFilenameForUser(User user) {
        return "tasks_" + user.getUsername() + ".txt";
    }

    private void saveTasksToFile(List<Task> tasks, User user) {
        String filename = getTaskFilenameForUser(user);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Task task : tasks) {
                writer.write(task.getDescription() + "," + task.getPriority() + "," + task.isCompleted());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Task> loadTasksFromFile(User user) {
        List<Task> tasks = new ArrayList<>();
        String filename = getTaskFilenameForUser(user);
        File file = new File(filename);
        if (!file.exists()) return tasks;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    Task task = new Task(parts[0], parts[1]);
                    task.setCompleted(Boolean.parseBoolean(parts[2]));
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    @Override
    public void start(Stage primaryStage) {

        LoginDialog loginDialog = new LoginDialog(primaryStage);
        currentUser = loginDialog.showAndWait();
        if (currentUser == null) {
            System.exit(0);
        }

        Label titleLabel = new Label("ToDo List Application - User: " + currentUser.getUsername());

        TextField inputField = new TextField();
        inputField.setPromptText("Enter a new task");
        inputField.setPrefWidth(180);

        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("Low", "Medium", "Hard");
        priorityBox.setValue("Low");
        priorityBox.setPrefWidth(100);

        todoList = new ListView<>();

        todoList.getItems().addAll(loadTasksFromFile(currentUser));

        Button addButton = new Button("Add");
        addButton.setPrefWidth(70);
        addButton.setOnAction(e -> {
            String taskText = inputField.getText().trim();
            String priority = priorityBox.getValue();
            if (!taskText.isEmpty() && priority != null) {
                Task newTask = new Task(taskText, priority);
                todoList.getItems().add(newTask);
                inputField.clear();
                priorityBox.setValue("Low");
                saveTasksToFile(todoList.getItems(), currentUser);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(70);
        deleteButton.setOnAction(e -> {
            Task selectedTask = todoList.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                todoList.getItems().remove(selectedTask);
                saveTasksToFile(todoList.getItems(), currentUser);
            }
        });

        Button completeButton = new Button("Mark as Completed");
        completeButton.setPrefWidth(130);
        completeButton.setOnAction(e -> {
            int selectedIdx = todoList.getSelectionModel().getSelectedIndex();
            if (selectedIdx != -1) {
                Task task = todoList.getItems().get(selectedIdx);
                if (!task.isCompleted()) {
                    task.setCompleted(true);
                    todoList.getItems().set(selectedIdx, task);
                    saveTasksToFile(todoList.getItems(), currentUser);
                }
            }
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(70);
        logoutButton.setOnAction(e -> {
            saveTasksToFile(todoList.getItems(), currentUser); // Çıkışta son hali kaydet
            primaryStage.close();
            Platform.runLater(() -> new Main().start(new Stage()));
        });

        HBox inputArea = new HBox(10, inputField, priorityBox, addButton, deleteButton, completeButton, logoutButton);
        inputArea.setPadding(new Insets(0, 0, 0, 0));

        VBox root = new VBox(15, titleLabel, inputArea, todoList);
        root.setPrefSize(650, 450);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root);
        primaryStage.setTitle("ToDo List");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}