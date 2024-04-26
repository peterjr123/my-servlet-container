package catalina.connector.http;

import catalina.connector.Connector;
import catalina.core.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

public class HttpConnector implements Runnable, Connector {
    private static final Logger log = LoggerFactory.getLogger(HttpConnector.class);
    private Stack<HttpProcessor> processors = new Stack<>();
    protected int minProcessors = 5;
    private int maxProcessors = 20;
    private int curProcessors = 0;
    private String scheme = "http";
    private static final int BUFFER_SIZE = 2048;
    private Container container;

    public HttpConnector() {
        while(curProcessors < minProcessors) {
            if((maxProcessors > 0) && (curProcessors >= maxProcessors)) {
                break;
            }
            processors.push(new HttpProcessor(this));
            curProcessors++;
        }
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
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                HttpProcessor processor = createProcessor();
                if(processor != null) {
                    processor.assign(socket);
                }
            } catch (IOException e) {
                log.debug("cannot accept connection", e);
                throw new RuntimeException(e);
            }
        }
    }

    private HttpProcessor createProcessor() {
        if(!processors.empty()) {
            return processors.pop();
        }
        else if(curProcessors < maxProcessors) {
            curProcessors++;
            return new HttpProcessor(this);
        }
        return null;
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public String getScheme() {
        return scheme;
    }


    @Override
    public void recycle(HttpProcessor processor) {
        processors.push(processor);
    }

    @Override
    public int getBufferSize() {
        return BUFFER_SIZE;
    }

    @Override
    public boolean isChunkingAllowed() {
        return true;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }
}
