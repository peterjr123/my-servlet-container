package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import catalina.lifecycle.Lifecycle;
import catalina.lifecycle.LifecycleListener;
import catalina.util.LifecycleSupport;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleContext implements Context, Pipeline, Lifecycle {
    private Loader loader;
    private Pipeline pipeline = new SimplePipeline(this);
    private Container parent;
    private Map<String, Container> children = new HashMap<>(); // wrapper name -> wrapper
    private Map<String, String> servletMapping = new HashMap<>(); // url path - > wrapper name
    private Mapper mapper = null;
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);
    protected boolean started = false;

    public SimpleContext() {
        pipeline.setBasic(new SimpleContextValve());
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

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setParent(Container parent) {

    }

    @Override
    public Container getParent() {
        return null;
    }

    @Override
    public void addChild(Container child) {
        child.setParent(this);
        children.put(child.getName(), child);
    }

    @Override
    public Container findChild(String name) {
        return children.get(name);
    }

    @Override
    public Container[] findChildren() {
        return children.values().toArray(new Container[0]);
    }

    @Override
    public void addMapper(Mapper mapper) {
        this.mapper = mapper;
        mapper.setContainer(this);
    }


    // --------------------------------- implementation of Context Interface ----------------------------------------------

    @Override
    public String findServletMapping(String path) {
        return servletMapping.get(path);
    }

    @Override
    public void addServletMapping(String path, String name) {
        servletMapping.put(path, name);
    }

    @Override
    public Container map(HttpRequest request, boolean update) {
        return mapper.map(request, update);
    }


    // ----------------------------------- implementation of Pipeline interface -------------------------------------

    @Override
    public Valve getBasic() {
        return null;
    }

    @Override
    public void setBasic(Valve basic) {

    }

    @Override
    public void addValve(Valve valve) {

    }

    @Override
    public void removeValve(Valve valve) {

    }

    // -------------------------- implementation of lifecycle interface ------------------------

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycle.addLifecycleListener(listener);
    }

    @Override
    public List<LifecycleListener> findLifecycleListeners() {
        return null;
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycle.removeLifecycleListener(listener);
    }

    @Override
    public void start() {
        System.out.println("SimpleContext start");
        if(started)
            throw new IllegalStateException("Context already started");
        lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
        started = true;

        try {
            if((loader != null) && (loader instanceof Lifecycle))
                ((Lifecycle)loader).start();

            Container Children[] = findChildren();
            for(Container child : Children) {
                if(child instanceof Lifecycle) {
                    ((Lifecycle)child).start();
                }
            }

            if(pipeline instanceof Lifecycle)
                ((Lifecycle)pipeline).start();
            lifecycle.fireLifecycleEvent(START_EVENT, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
    }

    @Override
    public void stop() {
        if(!started)
            throw new IllegalStateException("Context not started");

        lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        try {
            if(pipeline instanceof Lifecycle)
                ((Lifecycle)pipeline).stop();

            Container children[] = findChildren();
            for(Container child : children) {
                if(child instanceof Lifecycle) {
                    ((Lifecycle)child).stop();
                }
            }

            if((loader != null) && (loader instanceof Lifecycle))
                ((Lifecycle)loader).stop();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
    }
}
