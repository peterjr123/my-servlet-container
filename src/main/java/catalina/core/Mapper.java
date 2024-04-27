package catalina.core;

import catalina.connector.http.HttpRequest;

public interface Mapper {
    Container getContainer();
    void setContainer(Container container);
    String getProtocol();
    void setProtocol(String protocol);
    Container map(HttpRequest request, boolean update);
}
