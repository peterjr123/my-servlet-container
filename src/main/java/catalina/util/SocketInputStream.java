package catalina.util;

import catalina.connector.http.support.HttpRequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SocketInputStream extends InputStream {
    BufferedReader bufferedReader;

    public SocketInputStream(InputStream inputStream, int bufferSize) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream), bufferSize);
    }

    @Override
    public int read() throws IOException {
        return bufferedReader.read();
    }

    public void readRequestLine(HttpRequestLine requestLine) throws IOException {
        String line = bufferedReader.readLine();
        if (line != null) {
            requestLine.setRequestLine(line);
            String[] parsed = line.split(" ");
            if (parsed.length == 3) {
                requestLine.setMethod(parsed[0]);

                String uri = parsed[1];
                requestLine.setUri(uri);
                if(uri.contains("?")) {
                    requestLine.setQueryString(uri.substring(uri.indexOf("?")+1, uri.length()));
                }

                requestLine.setProtocol(parsed[2]);
            } else {
                throw new IOException("Invalid request line: " + line);
            }
        } else {
            throw new IOException("Invalid request line: null");
        }
    }
}
