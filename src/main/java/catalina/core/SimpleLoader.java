package catalina.core;

import catalina.connector.Constants;
import catalina.lifecycle.Lifecycle;
import catalina.lifecycle.LifecycleListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.List;

public class SimpleLoader implements Loader, Lifecycle {
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

    // --------------------------------- implementation of Lifecycle interface --------------------

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public List<LifecycleListener> findLifecycleListeners() {
        return null;
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public void start() {
        System.out.println("Starting SimpleLoader");
    }

    @Override
    public void stop() {

    }
}
