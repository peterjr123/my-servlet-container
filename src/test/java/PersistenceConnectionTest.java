import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PersistenceConnectionTest {
    static String fileRoot = System.getProperty("user.dir") + "/src/test/resources/";

    public static void main(String[] args) throws Exception {
        PersistenceConnectionTest test = new PersistenceConnectionTest();
        test.start();
    }

    public void start() throws Exception {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));

        Socket socket = serverSocket.accept();

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        String fileName = readRequest(inputStream);
        System.out.println("fileName: " + fileName);
        sendResponse(outputStream, fileName);

        while(true) {
            System.out.println("read Request... -------------------------");
            fileName = readPersistentRequest(inputStream);
            System.out.println("next fileName: " + fileName);
            sendResponse(outputStream, fileName);

            if(fileName.equals("css3.css")) {
                break;
            }
        }
    }

    @Test
    public void testSend() throws Exception {
        sendResponse(System.out, "css3.css");
    }

    public String readPersistentRequest(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        while(line != null && line.isEmpty()) {
            line = bufferedReader.readLine();
        }
        System.out.println(line);
        String url = line.substring(line.indexOf("/")+1, line.lastIndexOf(" "));

        while(true) {
            line = bufferedReader.readLine();
            if(line == null) {
                System.out.println("connection closed");
                break;
            }
            if(line.isEmpty())
                break;
            System.out.println(line);
        }

        return url;
    }

    public String readRequest(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = bufferedReader.readLine();
        String line;
        String url = requestLine.substring(requestLine.indexOf("/")+1, requestLine.lastIndexOf(" "));
        while(true) {
            line = bufferedReader.readLine();
            if(line == null) {
                System.out.println("connection closed");
                break;
            }
            if(line.isEmpty())
                break;
        }
        return url;
    }

    public void sendResponse(OutputStream outputStream, String fileName) throws Exception {
        int bufferSize = 1024;
        byte[] bytes = new byte[bufferSize];

        File file = new File(fileRoot, fileName);
        FileInputStream fileInputStream = new FileInputStream(file);

        outputStream.write("HTTP/1.1 200 OK\r\n".getBytes(StandardCharsets.UTF_8));
        if(fileName.contains(".css")) {
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
    }
}
