package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClientIPLoggerValve implements Valve, Contained {
    private static final Logger log = LoggerFactory.getLogger(ClientIPLoggerValve.class);
    protected Container container;

    @Override
    public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws ServletException, IOException {
        valveContext.invokeNext(request, response);

        log.debug("Client IP logger Valve");
        log.debug("Client IP: {}", request.getRemoteAddr());
        log.debug("----------------------------------------------------------");
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
