package model;

import java.util.Map;

/**
 * Wrapper object for http headers and body of the request.
 */
public class HttpRequest {

    private Map<String, String> headers;

    private HttpMethod httpMethod;

    private Integer contentLenght;

    private Integer actualContentLenght = 0;

    private byte[] content;

    public HttpRequest(Map<String, String> headers, HttpMethod httpMethod) {
        this.headers = headers;
        this.httpMethod = httpMethod;
        if(httpMethod.equals(HttpMethod.POST)) {
            this.contentLenght = Integer.valueOf(headers.get("Content-Length"));
            content = new byte[contentLenght];
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public byte[] getContent() {
        return content;
    }

    /**
     * @return number of bytes still missing
     */
    public int getMissingBytes() {
        return contentLenght - actualContentLenght;
    }

    /**
     * Adds content fragment to content and returns number of bytes still missing;
     * @param content content fragment
     * @return number of bytes still missing
     */
    public int addContent(byte[] content) {
        System.arraycopy(content, 0, this.content, actualContentLenght, content.length);
        actualContentLenght += content.length;
        return getMissingBytes();
    }
}
