import catalina.connector.http.HttpConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketTest implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SocketTest.class);
    String node = "node";

    public static void main(String[] args) {
        Thread server = new Thread(new SocketTest("server"));
        server.start();
    }

    public SocketTest() {}
    SocketTest(String node) {
        this.node = node;
    }

    public void server() throws Exception {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        Socket socket = serverSocket.accept();

        InputStream inputStream = socket.getInputStream();
        PersistentInputStream persistentInputStream = new PersistentInputStream(inputStream);
        OutputStream outputStream = socket.getOutputStream();
        SimpleOutputStream simpleOutputStream = new SimpleOutputStream(outputStream);

        while(true) {
            String msg = persistentInputStream.read();
            String requestLine = msg.split("\r\n")[0];
            log.debug("server received: {}", requestLine);

            String fileName = requestLine.split(" ")[1].substring(1);
            log.debug("fileName: {}", fileName);
            simpleOutputStream.sendResponse(fileName);
        }
    }

    @Override
    public void run() {
        if(node.equals("server")) {
            try {
                server();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
