import catalina.connector.Constants;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class ClassLoaderTest {
    @Test
    public void test() throws ServletException {
        ClassLoader classLoader = null;
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

        String actualClass = "servlet.PrimitiveServlet";

        ClassLoader loader = classLoader;

        Class<?> clazz;
        try {
            clazz = loader.loadClass(actualClass);
        }
        catch (ClassNotFoundException e) {
            throw new ServletException("could not load class: " + actualClass, e);
        }
    }
}
