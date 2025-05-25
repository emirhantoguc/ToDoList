package dsa_lab_solutions.todoproject;

public class Task {
    private String description;
    private String priority;
    private boolean completed;

    public Task(String description, String priority) {
        this.description = description;
        this.priority = priority;
        this.completed = false;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        String status = completed ? "✔️ " : "";
        return status + description + " [" + priority + "]";
    }
}