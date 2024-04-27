package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;

public class SimpleContextValve implements Valve, Contained{
    Container container;

    @Override
    public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws ServletException, IOException {
        Context context = (Context) getContainer();
        Wrapper wrapper = (Wrapper) context.map(request, true);
        wrapper.invoke(request, response);
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
