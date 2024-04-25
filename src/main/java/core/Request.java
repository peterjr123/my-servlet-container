package core;

import java.io.IOException;
import java.io.InputStream;

public class Request {
    private InputStream input;
    private String uri;

    public Request(InputStream input) {
        this.input = input;
    }

    public void parse() {
        StringBuffer request= new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        try{
            i = input.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for(int j = 0; j < i; j++){
            request.append(((char) buffer[j]));
        }
        System.out.println(request.toString());
        uri = parseUrl(request.toString());
    }

    private String parseUrl(String request) {
        int beforeIdx = request.indexOf(" ");
        if(beforeIdx != -1){
            int afterIdx = request.indexOf(" ", beforeIdx+1);
            if(afterIdx > beforeIdx){
                return request.substring(beforeIdx+1, afterIdx);
            }
        }
        return null;
    }

    public String getUri() {
        return uri;
    }
}
