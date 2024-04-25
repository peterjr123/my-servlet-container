package catalina.core.processor;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;

public class StaticResourceProcessor {
    public void process(HttpRequest request, HttpResponse response) {
        response.sendStaticResource();
    }
}
