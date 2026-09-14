import java.io.IOException;
import java.nio.file.*;
import java.util.*;

class Helper {
    static void printString(Path path) throws IOException {
        String str = Files.readString(path);
        System.out.println(str);
    }
}

public class TaskTracker {
    static {
        Path path = Path.of("task.json");

        try {
            if(!Files.exists(path) || Files.size(path) == 0) {
                Files.writeString(path, "[\n]", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Cannot create file task.json" + e.getMessage());
        }
    }
    public static void main(String[] args) throws IOException {
        Path path = Path.of("task.json");

        String json = """
                [
                    {"id": 1, "name": "laundry", "status": "todo"},
                    {"id": 2, "name": "cook", "status": "done"}
                ]
                """;
        Files.writeString(path, json);

        Helper.printString(path);
    }
}