package model;

/**
 * Enum for accepted http request paths.
 */
public enum HttpRequestPath {

    /**
     * Request path for sending new words.
     */
    DATA("/esw/myserver/data"),

    /**
     * Request path for getting number of unique words.
     */
    COUNT("/esw/myserver/count");


    private String name;

    HttpRequestPath(String name) {
        this.name = name;
    }
}
