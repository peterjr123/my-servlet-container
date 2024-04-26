import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PersistentInputStream {
    private static final Logger log = LoggerFactory.getLogger(PersistentInputStream.class);

    InputStreamReader input = null;
    boolean readable = false;

    public PersistentInputStream(InputStream in) {
        input = new InputStreamReader(in);
    }

    public String read() throws Exception {
        StringBuilder sb = new StringBuilder();
        log.debug("Server: Waiting for data...");
        while(!input.ready()) {
            Thread.sleep(100);
        }

        while(input.ready()) {
            int read = input.read();
            sb.append((char)read);
        }
        return sb.toString();
    }
}
