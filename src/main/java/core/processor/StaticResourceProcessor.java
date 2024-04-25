package core.processor;

import core.Request;
import core.Response;

public class StaticResourceProcessor {
    public void process(Request request, Response response) {
        response.sendStaticResource();
    }
}
