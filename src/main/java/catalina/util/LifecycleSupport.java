package catalina.util;

import catalina.lifecycle.Lifecycle;
import catalina.lifecycle.LifecycleEvent;
import catalina.lifecycle.LifecycleListener;

import java.util.List;
import java.util.ArrayList;

public class LifecycleSupport {
    private Lifecycle lifecycle;
    private LifecycleListener[] lifecycleListeners = new LifecycleListener[0];

    public LifecycleSupport(Lifecycle lifecycle) {
        super();
        this.lifecycle = lifecycle;
    }

    public void addLifecycleListener(LifecycleListener listener) {
        synchronized (lifecycleListeners) {
            LifecycleListener[] newListeners = new LifecycleListener[lifecycleListeners.length + 1];
            System.arraycopy(lifecycleListeners, 0, newListeners, 0, lifecycleListeners.length);
            newListeners[lifecycleListeners.length] = listener;
            lifecycleListeners = newListeners;
        }
    }

    public List<LifecycleListener> findLifecycleListeners() {
        return List.of(lifecycleListeners);
    }

    public void fireLifecycleEvent(String type, Object data) {
        LifecycleEvent event = new LifecycleEvent(lifecycle, type, data);
        LifecycleListener[] interested;
        synchronized (lifecycleListeners) {
            interested = lifecycleListeners.clone();
        }
        for(LifecycleListener listener : interested) {
            listener.lifecycleEvent(event);
        }
    }

    public void removeLifecycleListener(LifecycleListener listener) {
        synchronized (lifecycleListeners) {
            int n = -1;
            for(int i = 0; i < lifecycleListeners.length; i++) {
                if(lifecycleListeners[i] == listener) {
                    n = i;
                }
            }
            if(n < 0) {
                return;
            }

            LifecycleListener[] newListeners = new LifecycleListener[lifecycleListeners.length - 1];
            int j = 0;
            for(int i = 0; i < lifecycleListeners.length; i++) {
                if(i != n) {
                    newListeners[j++] = lifecycleListeners[i];
                }
            }
            lifecycleListeners = newListeners;
        }
    }
}
