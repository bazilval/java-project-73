package hexlet.code.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {
    public static String getResourceContent(String name) throws IOException {
        Path path = Paths.get("src/test/resources/fixtures/" + name + ".json");

        return Files.readAllLines(path).get(0);
    }
}
