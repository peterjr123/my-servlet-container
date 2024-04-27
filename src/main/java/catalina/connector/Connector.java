package catalina.connector;

import catalina.connector.http.HttpProcessor;
import catalina.core.Container;


public interface Connector {
    public void recycle(HttpProcessor processor);
    public int getBufferSize();

    boolean isChunkingAllowed();

    Container getContainer();

    void setContainer(Container container);
}
