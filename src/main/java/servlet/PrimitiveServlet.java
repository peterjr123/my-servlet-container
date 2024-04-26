package servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class PrimitiveServlet extends HttpServlet {
    static String fileRoot = System.getProperty("user.dir") + "/src/test/resources/";
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("Servlet init");
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        System.out.println("Servlet do service....");
        PrintWriter out = res.getWriter();
        try {
            writeFile(out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Servlet end service....");
    }

    private void writeFile(PrintWriter writer) throws IOException {
        int bufferSize = 1024;
        char[] bytes = new char[bufferSize];

        File file = new File(fileRoot, "index.html");
        FileReader fileReader = new FileReader(file);

        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/html");
        String contentLength = "Content-Length: " + file.length();
        writer.println(contentLength);
        writer.println("Connection: close");
        writer.println();

        int ch = fileReader.read(bytes, 0, bufferSize);
        while(ch != -1) {
            writer.write(bytes, 0, ch);
            ch = fileReader.read(bytes, 0, bufferSize);
        }
    }

    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void destroy() {
        System.out.println("Servlet destroy");
    }
}
