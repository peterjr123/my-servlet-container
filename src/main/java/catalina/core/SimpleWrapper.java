package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.lang.reflect.Constructor;

public class SimpleWrapper implements Wrapper, Pipeline {
    private Servlet instance;
    private Loader loader;
    private String servletClass;
    private String name;
    protected Container parent;
    private SimplePipeline pipeline = new SimplePipeline(this);

    public SimpleWrapper() {
        pipeline.setBasic(new SimpleWrapperValve());
    }

    // --------------------------------- implementation of Container Interface ----------------------------------------------

    @Override
    public void invoke(HttpRequest request, HttpResponse response) throws ServletException, IOException {
        pipeline.invoke(request, response);
    }

    @Override
    public Loader getLoader() {
        if(loader != null) {
            return loader;
        }
        if(parent != null) {
            return parent.getLoader();
        }
        return null;
    }

    @Override
    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setParent(Container parent) {
        this.parent = parent;
    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public void addChild(Container child) {
        throw new IllegalArgumentException("cannot add child container to wrapper");
    }

    @Override
    public Container findChild(String name) {
        return null;
    }

    @Override
    public void addMapper(Mapper mapper) {
        throw new IllegalArgumentException("cannot add mapper to wrapper");
    }

    // --------------------------------- implementation of Wrapper Interface ----------------------------------------------

    @Override
    public Servlet allocate() throws ServletException {
        if(instance == null) {
            instance = loadServlet();
        }
        return instance;
    }

    private Servlet loadServlet() throws ServletException {
        String actualClass = servletClass;
        if (actualClass == null) {
            throw new ServletException("servlet class has not been specified");
        }

        ClassLoader loader = getLoader().getClassLoader();

        Class<?> clazz;
        try {
            clazz = loader.loadClass(actualClass);
        }
        catch (ClassNotFoundException e) {
            throw new ServletException("could not load class: " + actualClass, e);
        }

        try {
            Constructor<?> constructor = clazz.getConstructor();
            return (Servlet) constructor.newInstance();
        }
        catch (Throwable e) {
            throw new ServletException("could not instantiate servlet: " + actualClass, e);
        }
    }

    @Override
    public void load() {

    }

    @Override
    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }


    // ----------------------------------- implementation of Pipeline interface -------------------------------------

    @Override
    public Valve getBasic() {
        return pipeline.getBasic();
    }

    @Override
    public void setBasic(Valve basic) {
        pipeline.setBasic(basic);
    }

    @Override
    public void addValve(Valve valve) {
        pipeline.addValve(valve);
    }

    @Override
    public void removeValve(Valve valve) {
        pipeline.removeValve(valve);
    }
}
