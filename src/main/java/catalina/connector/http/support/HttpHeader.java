package catalina.connector.http.support;

public class HttpHeader {
    String headerName;
    String headerValue;

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public boolean nameEquals(String name) {
        return headerName.equals(name);
    }

    public boolean valueEquals(String value) {
        return headerValue.equals(value);
    }
}
