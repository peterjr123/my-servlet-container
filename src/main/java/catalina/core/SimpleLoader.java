package catalina.core;

import catalina.connector.Constants;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class SimpleLoader implements Loader {
    ClassLoader classLoader;
    Container container;

    public SimpleLoader() {
        try {
            URL[] urls = new URL[1];
            File classPath = new File(Constants.SERVLET_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();

            URLStreamHandler streamHandler = null;
            urls[0] = new URL(null, repository, streamHandler);
            classLoader = new URLClassLoader(urls);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }


    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
