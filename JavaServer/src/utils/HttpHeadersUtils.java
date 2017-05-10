package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class helping with handling HTTP headers.
 */
public class HttpHeadersUtils {

    /**
     * Bulds HTTP headers from string lines.
     * @param headerLines lines
     * @return HTTP headers
     */
    public static Map<String, String> buildHttpHeaders(List<String> headerLines) {
        Map<String, String> httpHeaders = new HashMap<>();
        for (String line : headerLines) {
            String[] split = line.split(": ");
            httpHeaders.put(split[0], split[1]);
        }

        return httpHeaders;
    }
}
