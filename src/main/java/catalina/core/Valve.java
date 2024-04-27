package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;

public interface Valve {
    void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws ServletException, IOException;
}
