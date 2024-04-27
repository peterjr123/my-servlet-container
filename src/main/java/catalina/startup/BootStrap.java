package catalina.startup;

import catalina.connector.http.HttpConnector;
import catalina.core.*;

public final class BootStrap {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        Wrapper wrapper1 = new SimpleWrapper();
        wrapper1.setServletClass("servlet.PrimitiveServlet");
        wrapper1.setName("Primitive");
        Wrapper wrapper2 = new SimpleWrapper();
        wrapper2.setServletClass("servlet.ModernServlet");
        wrapper2.setName("Modern");

        Context context = new SimpleContext();
        context.addChild(wrapper1);
        context.addChild(wrapper2);

        Valve valve1 = new HeaderLoggerValve();
        Valve valve2 = new ClientIPLoggerValve();
        ((Pipeline)context).addValve(valve1);
        ((Pipeline)context).addValve(valve2);

        Mapper mapper = new SimpleContextMapper();
        mapper.setProtocol("http");
        context.addMapper(mapper);

        Loader loader = new SimpleLoader();
        context.setLoader(loader);

        context.addServletMapping("/servlet/PrimitiveServlet", "Primitive");
        context.addServletMapping("/servlet/ModernServlet", "Modern");

        connector.setContainer(context);

        connector.start();
    }
}
