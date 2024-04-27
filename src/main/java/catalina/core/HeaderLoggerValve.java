package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HeaderLoggerValve implements Valve {
    private static final Logger log = LoggerFactory.getLogger(HeaderLoggerValve.class);
    @Override
    public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws ServletException, IOException {
        valveContext.invokeNext(request, response);
        log.debug("Header Logger Value");
        log.debug("--------------------------------------------------------------");
    }
}
