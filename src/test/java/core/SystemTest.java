package core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class SystemTest {
    @Test
    public void test() {
        Assertions.assertTrue(Files.exists(Path.of(HttpServer.WEB_ROOT)));
    }
}
