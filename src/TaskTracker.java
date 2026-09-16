import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDateTime;

class Helper {
    static void printString() throws IOException {
        String str = Files.readString(TaskTracker.PATH);
        System.out.println(str);
    }

    static Task stringToTask(String line) {
        Task task = new Task();

        int start = line.indexOf("\"id\": ") + "\"id\": ".length();
        int end = line.indexOf(", \"description\"", start);
        task.setId(Integer.parseInt(line.substring(start, end)));

        start = line.indexOf("\"description\": ") + "\"description\": ".length() + 1;
        end = line.indexOf(", \"status\"", start) - 1;
        task.setDescription(line.substring(start, end));

        start = line.indexOf("\"status\": ") + "\"status\": ".length() + 1;
        end = line.indexOf(", \"createdAt\"", start) - 1;
        task.setStatus(line.substring(start, end));

        start = line.indexOf("\"createdAt\": ") + "\"createdAt\": ".length() + 1;
        end = line.indexOf(", \"updatedAt\"", start) - 1;
        task.setCreatedAt(LocalDateTime.parse(line.substring(start, end)));

        start = line.indexOf("\"updatedAt\": ") + "\"updatedAt\": ".length() + 1;
        end = line.length() - 1;
        task.setUpdatedAt(LocalDateTime.parse(line.substring(start, end)));

        return task;
    }

    static String taskToString(Task task) {
        return "{\"id\": " + task.getId()
                + ", \"description\": \"" + task.getDescription()
                + "\", \"status\": \"" + task.getStatus()
                + "\", \"createdAt\": \"" + task.getCreatedAt()
                + "\", \"updatedAt\": \"" + task.getUpdatedAt()
                + "\"}";
    }

    static List<Task> readData () throws IOException {
        String content = Files.readString(TaskTracker.PATH).strip();
        content = content.substring(1,content.length()-1).strip();

        String[] parts = content.split("},\\s*\\{");

        parts = Arrays.stream(parts)
                .map(object -> object.replace("{", "").replace("}", "").strip())
                .toArray(String[]::new);

        List<Task> tasks = new ArrayList<>();

        for(String object : parts) {
            tasks.add(stringToTask(object));
        }

        return tasks;
    }

    static void add(String description) throws IOException {
        List<Task> tasks = readData();

        int maxId = 0;

        for (Task task : tasks) {
            maxId = Math.max(maxId, task.getId());
        }

        Task task = new Task(maxId + 1, description);
        tasks.add(task);

        StringBuilder str = new StringBuilder("[\n");

        for (int i = 0; i < tasks.size(); i++) {
            str.append("    ")
               .append(Helper.taskToString(tasks.get(i)));

            if (i < tasks.size() - 1) {
                str.append(",");
            }

            str.append("\n");
        }

        str.append("]");

        Files.writeString(
                TaskTracker.PATH,
                str.toString(),
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}

class Task {
    private int id;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task() {
        id = 0;
        description = "";
        status = "";
        createdAt = null;
        updatedAt = null;
    }

    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.status = "todo";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Id: " + id
                + ", Description: " + description
                + ", Status: " + status
                + ", CreatedAt: " + createdAt
                + ", UpdatedAt: " + updatedAt;
    }
}

public class TaskTracker {
    static final Path PATH = Path.of("task.json");

    static {
        try {
            if(!Files.exists(PATH) || Files.size(PATH) == 0) {
                Files.writeString(PATH, "[\n]", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Cannot create file task.json" + e.getMessage());
        }
    }
    public static void main(String[] args) throws IOException {
        List<Task> tasks = Helper.readData();

        for(Task task : tasks) {
            System.out.println(task);
        }
    }
}