package model;

/**
 * Enum for accepted http methods.
 */
public enum HttpMethod {

    GET("GET"), POST("POST");

    private String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
