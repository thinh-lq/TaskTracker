import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDateTime;

class Helper {
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

    static List<Task> readData() throws IOException {
        String content = Files.readString(TaskTracker.PATH).strip();
        content = content.substring(1,content.length()-1).strip();

        String[] parts = content.split("},\\s*\\{");

        List<Task> tasks = new ArrayList<>();

        if(content.isEmpty()) {
            return tasks;
        }

        parts = Arrays.stream(parts)
                .map(object -> object.replace("{", "").replace("}", "").strip())
                .toArray(String[]::new);

        for(String object : parts) {
            tasks.add(stringToTask(object));
        }

        return tasks;
    }

    static void writeData() throws IOException {
        List<Task> tasks = TaskTracker.tasks;
        StringBuilder str = new StringBuilder("[\n");

        for(int i = 0; i < tasks.size(); i++) {
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

    static void add(String description) throws IOException {
        List<Task> tasks = TaskTracker.tasks;

        int maxId = 0;

        for (Task task : tasks) {
            maxId = Math.max(maxId, task.getId());
        }

        Task task = new Task(maxId + 1, description);
        tasks.add(task);

        writeData();
    }

    static void delete(int id) throws IOException {
        List<Task> tasks = TaskTracker.tasks;

        for(int i = 0 ; i < tasks.size() ; i++) {
            if(tasks.get(i).getId() == id) {
                tasks.remove(i);
                writeData();
                return;
            }
        }

        System.out.println("Task not found");
    }

    static void list(String status) {
        for(Task task : TaskTracker.tasks) {
            if (status == null || task.getStatus().equals(status)) {
                System.out.println(task);
            }
        }
    }

    static void update(int id, String description) throws IOException {
        for(Task task : TaskTracker.tasks) {
            if(task.getId() == id) {
                task.setDescription(description);
                task.setUpdatedAt(LocalDateTime.now());
                writeData();
                return;
            }
        }

        System.out.println("Task not found");
    }

    static void mark(int id, String status) throws IOException {
        for(Task task : TaskTracker.tasks) {
            if(task.getId() == id) {
                task.setStatus(status);
                task.setUpdatedAt(LocalDateTime.now());
                writeData();
                return;
            }
        }

        System.out.println("Task not found");
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
    static List<Task> tasks;

    static {
        try {
            if (!Files.exists(PATH) || Files.size(PATH) == 0) {
                Files.writeString(
                        PATH,
                        "[\n]",
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            }

            tasks = Helper.readData();

        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Cannot create file task.json" + e.getMessage()
            );
        }
    }

    static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No command provided");
            return;
        }

        String command = args[0];

        switch (command) {
            case "add":
                if (args.length < 2) {
                    System.out.println("Description is required");
                    return;
                }

                Helper.add(args[1]);
                break;

            case "update":
                if (args.length < 3) {
                    System.out.println("Id and description are required");
                    return;
                }

                int updateId;

                try {
                    updateId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid id");
                    return;
                }

                Helper.update(updateId, args[2]);
                break;

            case "delete":
                if (args.length < 2) {
                    System.out.println("Id is required");
                    return;
                }

                int deleteId;

                try {
                    deleteId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid id");
                    return;
                }

                Helper.delete(deleteId);
                break;

            case "mark-in-progress":
                if (args.length < 2) {
                    System.out.println("Id is required");
                    return;
                }

                int inProgressId;

                try {
                    inProgressId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid id");
                    return;
                }

                Helper.mark(inProgressId, "in-progress");
                break;

            case "mark-done":
                if (args.length < 2) {
                    System.out.println("Id is required");
                    return;
                }

                int doneId;

                try {
                    doneId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid id");
                    return;
                }

                Helper.mark(doneId, "done");
                break;

            case "list":
                if (args.length == 1) {
                    Helper.list(null);
                    return;
                }

                String status = args[1];

                if (!status.equals("done") && !status.equals("todo") && !status.equals("in-progress")) {
                    System.out.println("Invalid status");
                    return;
                }

                Helper.list(status);
                break;

            default:
                System.out.println("Unknown command");
        }
    }
}