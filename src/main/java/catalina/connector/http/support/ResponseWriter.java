package catalina.connector.http.support;

import java.io.PrintWriter;
import java.io.Writer;

public class ResponseWriter extends PrintWriter {
    public ResponseWriter(Writer out) {
        super(out, true);
    }

    // Override print method to be flushed after
    @Override
    public void print(String s) {
        super.print(s);
        super.flush();
    }
}
