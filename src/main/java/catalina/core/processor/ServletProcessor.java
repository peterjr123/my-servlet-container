package catalina.core.processor;

import catalina.connector.Constants;
import catalina.connector.http.*;
import catalina.core.*;
import jakarta.servlet.Servlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;


public class ServletProcessor {
    private static final Logger log = LoggerFactory.getLogger(ServletProcessor.class);

    public void process(HttpRequest request, HttpResponse response) {

        String uri = request.getRequestURI();
        String servletName = uri.substring(uri.lastIndexOf('/') + 1);
        log.debug("servletName: {}", servletName);

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
            servlet.service(new HttpRequestFacade(request), new HttpResponseFacade(response));
            response.flushBuffer();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
