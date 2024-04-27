package catalina.core;

import catalina.connector.http.HttpRequest;
import catalina.connector.http.HttpResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimplePipeline implements Pipeline {
    List<Valve> valves = new ArrayList<>();
    Valve basic;
    Container container;

    public SimplePipeline(Container container) {
        this.container = container;
    }

    @Override
    public Valve getBasic() {
        return basic;
    }

    @Override
    public void setBasic(Valve basic) {
        this.basic = basic;
        ((Contained) basic).setContainer(container);
    }

    @Override
    public void addValve(Valve valve) {
        if(valve instanceof Contained) {
            ((Contained) valve).setContainer(container);
        }
        valves.add(valve);
    }

    @Override
    public void invoke(HttpRequest request, HttpResponse response) throws ServletException, IOException {
        (new StandardPipelineValveContext()).invokeNext(request, response);
    }

    @Override
    public void removeValve(Valve valve) {
        valves.remove(valve);
    }

    class StandardPipelineValveContext implements ValveContext {
        protected int stage = 0;

        @Override
        public void invokeNext(HttpRequest request, HttpResponse response) throws IOException, ServletException {
            int subscript = stage;
            stage++;

            if(subscript < valves.size()) {
                valves.get(subscript).invoke(request, response, this);
            }
            else if(subscript == valves.size()) {
                basic.invoke(request, response, this);
            }
            else {
                throw new ServletException("no value at pipeline");
            }
        }
    }
}
