package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;

public interface Pipeline {
    Valve getBasic();
    void setBasic(Valve basic);
    void addValve(Valve valve);
    void invoke(HttpRequest request, HttpResponse response) throws ServletException, IOException;
    void removeValve(Valve valve);
}
