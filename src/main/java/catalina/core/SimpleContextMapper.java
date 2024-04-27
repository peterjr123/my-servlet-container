package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleContextMapper implements Mapper{
    public static final Logger log = LoggerFactory.getLogger(SimpleContextMapper.class);
    private SimpleContext context = null;
    @Override
    public Container getContainer() {
        return context;
    }

    @Override
    public void setContainer(Container container) {
        if(!(container instanceof SimpleContext)){
            throw new IllegalArgumentException("Container of Mapper must be a SimpleContext");
        }
        context = (SimpleContext) container;
    }

    @Override
    public String getProtocol() {
        return "";
    }

    @Override
    public void setProtocol(String protocol) {

    }

    @Override
    public Container map(HttpRequest request, boolean update) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String relativeURI = requestURI.substring(contextPath.length());
        log.debug("contextPath: {}, requestURI: {}, relativeURI: {}", contextPath, requestURI, relativeURI);

        Wrapper wrapper = null;
        String servletPath = relativeURI;
        String pathInfo = null;
        String name = context.findServletMapping(relativeURI);
        if(name != null)
            wrapper = (Wrapper) context.findChild(name);
        return wrapper;
    }
}
