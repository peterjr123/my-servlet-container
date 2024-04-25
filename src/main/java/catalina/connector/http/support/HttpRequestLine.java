package catalina.connector.http.support;

public class HttpRequestLine {
    String requestLine;
    String method;
    String uri;
    String protocol;
    String queryString;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public int indexOf(String str) {
        return requestLine.indexOf(str);
    }

    public void setRequestLine(String requestLine) {
        this.requestLine = requestLine;
    }
}
