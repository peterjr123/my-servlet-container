package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;

public interface ValveContext {
    void invokeNext(HttpRequest request, HttpResponse response) throws ServletException, IOException;
}
