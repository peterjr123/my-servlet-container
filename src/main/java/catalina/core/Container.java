package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;

public interface Container {
    void invoke(HttpRequest request, HttpResponse response);
}
