import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SimpleOutputStream {
    static String fileRoot = System.getProperty("user.dir") + "/src/test/resources/";

    OutputStream outputStream;
    public SimpleOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendResponse(String fileName) throws Exception {
        int bufferSize = 1024;
        byte[] bytes = new byte[bufferSize];

        File file = new File(fileRoot, fileName);
        FileInputStream fileInputStream = new FileInputStream(file);

        outputStream.write("HTTP/1.1 200 OK\r\n".getBytes(StandardCharsets.UTF_8));
        if(fileName.contains("css")) {
            outputStream.write("Content-Type: text/css\r\n".getBytes(StandardCharsets.UTF_8));
        }
        else {
            outputStream.write("Content-Type: text/html\r\n".getBytes(StandardCharsets.UTF_8));
        }
        String contentLength = "Content-Length: " + file.length() + "\r\n";
        outputStream.write(contentLength.getBytes(StandardCharsets.UTF_8));
        outputStream.write("Connection: Keep-Alive\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write("Keep-Alive: timeout=5\r\n".getBytes(StandardCharsets.UTF_8));
        outputStream.write("\r\n".getBytes(StandardCharsets.UTF_8));

        int ch = fileInputStream.read(bytes, 0, bufferSize);
        while(ch != -1) {
            outputStream.write(bytes, 0, ch);
            ch = fileInputStream.read(bytes, 0, bufferSize);
        }

        outputStream.flush();
    }
}
