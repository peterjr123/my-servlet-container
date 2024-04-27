package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimpleContext implements Context, Pipeline {
    private Loader loader;
    private Pipeline pipeline = new SimplePipeline(this);
    private Container parent;
    private Map<String, Container> children = new HashMap<>(); // wrapper name -> wrapper
    private Map<String, String> servletMapping = new HashMap<>(); // url path - > wrapper name
    private Mapper mapper = null;

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


}
