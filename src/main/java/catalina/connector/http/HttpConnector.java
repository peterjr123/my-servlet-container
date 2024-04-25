package catalina.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpConnector implements Runnable {
    private String scheme = "http";

    public String getScheme() {
        return scheme;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while(true) {
            try(Socket socket = serverSocket.accept()) {
                HttpProcessor processor = new HttpProcessor();
                processor.process(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }
}
