package catalina.core;

public interface Contained {
    public Container getContainer();
    public void setContainer(Container container);
}
