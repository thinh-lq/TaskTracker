import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class TaskTracker {
    public static void main(String[] args) throws IOException {
        Path path = Path.of("task.json");

        String json = """
                [
                    {
                        "id": 1,
                        "status": "todo"
                    }
                    {
                        "id": 2,
                        "status": "done"
                    }
                ]
                """;
        Files.writeString(path, json);
    }
}