package catalina.lifecycle;



import java.util.List;

public interface Lifecycle {
    String START_EVENT = "start";
    String BEFORE_START_EVENT = "before_start";
    String AFTER_START_EVENT = "after_start";
    String STOP_EVENT = "stop";
    String BEFORE_STOP_EVENT = "before_stop";
    String AFTER_STOP_EVENT = "after_stop";

    void addLifecycleListener(LifecycleListener listener);
    List<LifecycleListener> findLifecycleListeners();
    public void removeLifecycleListener(LifecycleListener listener);
    public void start();
    public void stop();
}
