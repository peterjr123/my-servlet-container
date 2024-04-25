package catalina.startup;

import catalina.connector.http.HttpConnector;

public final class BootStrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }
}
