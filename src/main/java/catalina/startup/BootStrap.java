package catalina.startup;

import catalina.connector.http.HttpConnector;
import catalina.core.SimpleContainer;

public final class BootStrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        SimpleContainer container = new SimpleContainer();
        connector.setContainer(container);

        connector.start();
    }
}
