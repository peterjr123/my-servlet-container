package core;

import java.io.*;

public class Response {
    private static final int BUFFER_SIZE = 1024;
    Request request;
    OutputStream output;
    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendStaticResource() {
        byte[] buffer = new byte[BUFFER_SIZE];
        File file = new File(HttpServer.WEB_ROOT, request.getUri());
        if(file.exists()) {
            try(FileInputStream fis = new FileInputStream(file)) {
                int ch = fis.read(buffer, 0, BUFFER_SIZE);
                while(ch != -1) {
                    output.write(buffer, 0, ch);
                    ch = fis.read(buffer, 0, BUFFER_SIZE);
                }
            }
            catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            String errorMessage = "HTTP/1.1 404 File not found\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Content-Length: 23\r\n"
                    + "\r\n"
                    + "<h1>404 File Not Found</h1>\r\n";
            try {
                output.write(errorMessage.getBytes());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
