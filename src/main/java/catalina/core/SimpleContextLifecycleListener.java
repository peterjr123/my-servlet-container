package catalina.core;

import catalina.lifecycle.Lifecycle;
import catalina.lifecycle.LifecycleEvent;
import catalina.lifecycle.LifecycleListener;

public class SimpleContextLifecycleListener implements LifecycleListener {

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        Lifecycle lifecycle = event.getLifecycle();
        System.out.println("SimpleContextLifecycleListener: " + event.getType().toString());

        if(Lifecycle.START_EVENT.equals(event.getType())) {
            System.out.println("starting context");
        }
        else if(Lifecycle.STOP_EVENT.equals(event.getType())) {
            System.out.println("stopping context");
        }
    }
}
