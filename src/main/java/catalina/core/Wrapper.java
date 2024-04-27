package catalina.core;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;

public interface Wrapper extends Container {
    public Servlet allocate() throws ServletException;
    public void load();
    public void setServletClass(String servletClass);
}
