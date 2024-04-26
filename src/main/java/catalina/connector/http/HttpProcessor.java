package catalina.connector.http;

import catalina.connector.Connector;
import catalina.connector.http.support.HttpHeader;
import catalina.connector.http.support.HttpRequestLine;

import catalina.util.SocketInputStream;
import catalina.util.StringManager;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(HttpProcessor.class);
    private HttpRequestLine requestLine = new HttpRequestLine();
    private HttpHeader header = new HttpHeader();
    private HttpRequest request = null;
    private HttpResponse response = null;
    private Socket socket = null;
    private Connector connector = null;
    // set to true when socket is assigned
    private boolean available = false;
    private boolean keepAlive = true;
    private boolean http11 = true;
    private boolean finishResponse = true;

    public HttpProcessor(Connector connector) {
        this.connector = connector;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while(true) {
            Socket socket = await();
            if(socket == null)
                continue;

            try {
                log.debug("processing request with new process");
                process(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            log.debug("recycle old process");
            connector.recycle(this);
        }
    }

    synchronized void assign(Socket socket) {
        while(available) {
            try {
                wait();
            }
            catch (InterruptedException e) {

            }
        }

        this.socket = socket;
        available = true;
        notifyAll();
    }

    private synchronized Socket await() {
        while(!available) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        Socket socket = this.socket;
        available = false;
        notifyAll();

        return socket;
    }


    public void process(Socket socket) throws IOException {
        try (SocketInputStream input = new SocketInputStream(socket.getInputStream(), connector.getBufferSize());
             OutputStream output = socket.getOutputStream()) {
            while(keepAlive) {
                log.debug("create new request&response");
                request = new HttpRequest(input);
                response = new HttpResponse(output);
                response.setRequest(request);
                response.setHeader("Server", "My Servlet Container");

                log.debug("parse request&header");
                try {
                    parseRequest(input, output);
                }
                catch (Exception e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    continue;
                }
                parseHeaders(input);

                if(http11) {
                    ackRequest(output);
                    if(connector.isChunkingAllowed())
                        response.setAllowChunking(true);
                }

                connector.getContainer().invoke(request, response);

                output.flush();

                if("close".equals(response.getHeader("Connection"))) {
                    keepAlive = false;
                }
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        finally {
            socket.close();
        }
    }



    private void parseHeaders(SocketInputStream input) throws IOException {
        while(input.readHeader(header) != -1) {
            if(header.nameEquals("Cookie")) {
                // parse cookies..
            }
            else if(header.nameEquals("Connection")) {
                if(header.valueEquals("close")) {
                    keepAlive = false;
                    response.setHeader("Connection", "close");
                }
            }
        }
    }

    private void parseRequest(SocketInputStream input, OutputStream output) throws ServletException, IOException {
        input.readRequestLine(requestLine);
        String method = requestLine.getMethod();
        String uri = null;
        String protocol = requestLine.getProtocol();

        if(method.length() < 1) {
            throw new ServletException("Missing HTTP method");
        }
        else if(requestLine.getUri() == null) {
            throw new ServletException("Missing HTTP request uri");
        }

        // currently uri = "http://www.hello.com/hello/say%20Hello;jsessionid=123?name=peter
        int question = requestLine.indexOf("?");
        if(question >= 0) {
            request.setQueryString(requestLine.getQueryString());
            uri = requestLine.getUri().substring(0, question);
        }
        else {
            request.setQueryString(null);
            uri = requestLine.getUri();
        }

        // currently uri = "http://www.hello.com/hello/say%20Hello;jsessionid=123
        if(!uri.startsWith("/")) {
            int pos = uri.indexOf("://");
            if(pos != -1) {
                pos = uri.indexOf("/", pos+3);
                if(pos == -1) {
                    uri = "";
                }
                else {
                    uri = uri.substring(pos);
                }
            }
        }

        // currently uri = "/hello/say%20Hello;jsessionid=123
        String match = ";jsessionid=";
        int semicolon = uri.indexOf(match);
        if(semicolon > 0) {
            String rest = uri.substring(semicolon + match.length());
            int semicolon2 = rest.indexOf(";");
            if(semicolon2 > 0) {
                request.setRequestSessionId(rest.substring(0, semicolon2));
                rest = rest.substring(semicolon2);
            }
            else {
                request.setRequestSessionId(rest.substring(semicolon2));
                rest = "";
            }
            request.setRequestSessionURL(true);
            uri = uri.substring(0, semicolon) + rest;
        }
        else {
            request.setRequestSessionId(null);
            request.setRequestSessionURL(false);
        }

        // currently uri = "/hello/say%20Hello
        String normalizedUri = normalize(uri);
        request.setMethod(method);
        request.setProtocol(protocol);
        if(normalizedUri != null) {
            // saved uri = "/hello/say Hello"
            request.setRequestURI(normalizedUri);
        }
        else {
            request.setRequestURI(uri);
        }

        // if uri is not standard form (unable to normalize)
        if(normalizedUri == null) {
            throw new ServletException("Invalid URI: " + uri + "'");
        }
    }

    private String normalize(String uri) {
        // need to normalize
        return uri;
    }

    private void ackRequest(OutputStream output) {
    }

    private void parseConnection(Socket socket) {

    }




}
