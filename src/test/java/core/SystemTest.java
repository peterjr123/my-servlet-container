package core;

import catalina.connector.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class SystemTest {
    private static final Logger log = LoggerFactory.getLogger(SystemTest.class);
    @Test
    public void test() {
        Assertions.assertTrue(Files.exists(Path.of(Constants.WEB_ROOT)));
    }

    @Test
    public void testLog() {
        for(int i = 0; i < 3; i++) {
            log.debug("log!: {}", i);
        }
    }
}
