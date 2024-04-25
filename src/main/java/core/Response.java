package core;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;

import java.io.*;
import java.util.Locale;

public class Response implements ServletResponse {
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
        File file = new File(Constants.WEB_ROOT, request.getUri());
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

    @Override
    public String getCharacterEncoding() {
        return "";
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(output, true);
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public void setContentLength(int len) {

    }

    @Override
    public void setContentLengthLong(long len) {

    }

    @Override
    public void setContentType(String type) {

    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
