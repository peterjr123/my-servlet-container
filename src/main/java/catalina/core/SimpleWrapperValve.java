package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;

import java.io.IOException;

public class SimpleWrapperValve implements Valve, Contained {
    Container container;

    @Override
    public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws ServletException, IOException {
        SimpleWrapper wrapper = (SimpleWrapper) getContainer();
        Servlet servlet = wrapper.allocate();
        servlet.service(request, response);
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
