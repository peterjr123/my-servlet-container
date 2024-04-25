package catalina.connector.http;

import catalina.connector.http.support.HttpRequestLine;
import catalina.core.processor.ServletProcessor;
import catalina.core.processor.StaticResourceProcessor;

import catalina.util.SocketInputStream;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor {
    private static final Logger log = LoggerFactory.getLogger(HttpProcessor.class);
    private HttpRequestLine requestLine = new HttpRequestLine();
    private HttpRequest request = null;
    private HttpResponse response = null;

    public void process(Socket socket) {
        try (SocketInputStream input = new SocketInputStream(socket.getInputStream(), 2048);
             OutputStream output = socket.getOutputStream()) {

            request = new HttpRequest(input);
            response = new HttpResponse(output);
            response.setRequest(request);
            response.setHeader("Server", "My Servlet Container");
            parseRequest(input, output);
            parseHeaders(input);

            log.debug("uri: {}", request.getRequestURI());
            // passes request&response to container
            if(request.getRequestURI().startsWith("/servlet")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            }
            else {
                StaticResourceProcessor processor = new StaticResourceProcessor();
                processor.process(request, response);
            }
        }
        catch (IOException | ServletException e) {
            System.out.println(e.getMessage());
        }
    }

    private void parseHeaders(SocketInputStream input) {

    }

    private void parseRequest(SocketInputStream input, OutputStream output) throws ServletException, IOException {
        input.readRequestLine(requestLine);
        String method = requestLine.getMethod();
        String uri = null;
        String protocol = requestLine.getProtocol();

        if(method.length() < 1) {
            throw new ServletException("Missing HTTP request method");
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
}
