package catalina.core;

import catalina.connector.http.HttpRequest;

public interface Context extends Container {
    String findServletMapping(String path);
    void addServletMapping(String path, String name);
    Container map(HttpRequest request, boolean update);
}
