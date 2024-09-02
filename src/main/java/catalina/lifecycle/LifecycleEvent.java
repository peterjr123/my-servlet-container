package catalina.lifecycle;

import java.util.EventObject;

public final class LifecycleEvent extends EventObject {
    Lifecycle lifecycle;
    String type;
    Object data;

    public LifecycleEvent(Lifecycle lifecycle, String type) {
        this(lifecycle, type, null);
    }

    public LifecycleEvent(Lifecycle lifecycle, String type, Object data) {
        super(lifecycle);

        this.lifecycle = lifecycle;
        this.type = type;
        this.data = data;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }
    public String getType() {
        return type;
    }
    public Object getData() {
        return data;
    }
}
