package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import catalina.lifecycle.Lifecycle;
import catalina.lifecycle.LifecycleListener;
import catalina.util.LifecycleSupport;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;

public class SimpleWrapper implements Wrapper, Pipeline, Lifecycle {
    private Servlet instance;
    private Loader loader;
    private String servletClass;
    private String name;
    protected Container parent;
    private SimplePipeline pipeline = new SimplePipeline(this);
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);
    protected boolean started = false;

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
    public Container[] findChildren() {
        return new Container[0];
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

    // ------------------------------------ implementation of Lifecycle interface --------------------------------------

    @Override
    public void addLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public List<LifecycleListener> findLifecycleListeners() {
        return List.of();
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {

    }

    @Override
    public void start() {
        System.out.println("starting wrapper " + name);
        if(started)
            throw new IllegalStateException("wrapper is already started");
        lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
        started = true;

        if((loader != null) && (loader instanceof Lifecycle)) {
            ((Lifecycle) loader).start();
        }
        if(pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).start();
        }
        lifecycle.fireLifecycleEvent(START_EVENT, null);
        lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
    }

    @Override
    public void stop() {
        System.out.println("stopping wrapper " + name);
        try {
            instance.destroy();
        }
        catch (Throwable e) {}

        instance = null;
        if(!started)
            throw new IllegalStateException("wrapper is not started");

        lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        if(pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).stop();
        }
        if((loader != null) && (loader instanceof Lifecycle)) {
            ((Lifecycle) loader).stop();
        }

        lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
    }
}
