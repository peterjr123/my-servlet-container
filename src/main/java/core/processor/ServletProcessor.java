package core.processor;

import core.*;
import jakarta.servlet.Servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.logging.StreamHandler;


public class ServletProcessor {
    public void process(Request request, Response response) {
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf('/') + 1);
        URLClassLoader loader = null;
        try {
            URL[] urls = new URL[1];
            File classPath = new File(Constants.SERVLET_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            System.out.println(repository);

            URLStreamHandler streamHandler = null;
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        }
        catch (IOException e) {
            System.out.println(e);
        }

        Class clazz = null;
        try {
            clazz = loader.loadClass(Constants.SERVLET_PACKAGE + "." + servletName);
        }
        catch (ClassNotFoundException e) {
            System.out.println(e);
        }

        Servlet servlet = null;
        try {
            Constructor constructor = clazz.getConstructor();
            servlet = (Servlet) constructor.newInstance();
            servlet.service(new RequestFacade(request), new ResponseFacade(response));
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
