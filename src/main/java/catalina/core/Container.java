package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;

public interface Container {
    void invoke(HttpRequest request, HttpResponse response) throws ServletException, IOException;
    Loader getLoader();
    void setLoader(Loader loader);

    void setName(String name);
    String getName();

    void setParent(Container parent);
    Container getParent();

    void addChild(Container child);
    Container findChild(String name);

    void addMapper(Mapper mapper);
}
