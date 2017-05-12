package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class helping with handling HTTP headers.
 */
public class HttpUtils {

    /**
     * Bulds HTTP headers from string lines.
     * @param headerLines lines
     * @return HTTP headers
     */
    public static Map<String, String> parseHttpHeaders(List<String> headerLines) {
        Map<String, String> httpHeaders = new HashMap<String, String>();
        for (String line : headerLines) {
            String[] split = line.split(": ");
            httpHeaders.put(split[0], split[1]);
        }

        return httpHeaders;
    }

    /**
     * @return HTTP response 400 - Bad Request message
     */
    public static String createHttpBadRequestResponse() {
        return createHttpResponse(400, "Bad Request", null);
    }

    /**
     * Creates HTTP 1.1 response.
     *
     * @param responseStatusCode response status code
     * @param responseReason response reason
     * @param responseContent nullable response content
     * @return HTTP response message
     */
    public static String createHttpResponse(int responseStatusCode, String responseReason, String responseContent) {
        StringBuilder httpResponse = new StringBuilder();

        // first line
        httpResponse.append("HTTP/1.1 ");
        httpResponse.append(responseStatusCode);
        httpResponse.append(" ");
        httpResponse.append(responseReason);
        httpResponse.append("\r\n");

        if (responseContent != null) {
            // headers
            httpResponse.append("Content-Length: ");
            httpResponse.append(responseContent.length());
            httpResponse.append("\r\n");

            httpResponse.append("Connection: Closed\r\n");
            httpResponse.append("Content-Type: text/plain\r\n");

            httpResponse.append("\r\n");

            // content
            httpResponse.append(responseContent);
        } else {
            httpResponse.append("\r\n");
        }

        return httpResponse.toString();
    }
}
