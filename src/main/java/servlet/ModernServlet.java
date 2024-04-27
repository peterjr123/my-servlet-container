package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class ModernServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        String body = "<h1>ModernServlet</h1>";
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-Length: " + body.length());
        out.println("Connection: keep-alive");
        out.println("Keep-Alive: timeout=5");
        out.println();
        out.println(body);
    }
}
