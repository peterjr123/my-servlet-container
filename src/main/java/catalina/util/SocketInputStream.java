package catalina.util;

import catalina.connector.http.HttpProcessor;
import catalina.connector.http.support.HttpHeader;
import catalina.connector.http.support.HttpRequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SocketInputStream extends InputStream {
    BufferedReader bufferedReader;
    private static final Logger log = LoggerFactory.getLogger(SocketInputStream.class);

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
            log.debug("Read request line: {}", line);
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
            throw new IOException("input data is null");
        }
    }

    public int readHeader(HttpHeader header) throws IOException {
        String line = bufferedReader.readLine();
        if(!line.isEmpty()) {
            int nameEnd = line.indexOf(' ');
            if(nameEnd > 0) {
                log.debug("header: {}", line);
                header.setHeaderName(line.substring(0, nameEnd));
                header.setHeaderValue(line.substring(nameEnd+1));
                return 0;
            }
        }
        return -1;
    }
}
