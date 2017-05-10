package settings;

/**
 * Configuration class for server.
 */
public class ServerSettings {

    private static String host = "localhost";

    private static Integer port = 8080;

    private static Integer bufferSize = 1000000;

    private static Integer bufferPoolInitialSize = 32;

    private static Integer bufferPoolIncrement = 16;

    private static Integer numberOfNioWorkers = 5;

    private static Integer numberOfNioReactors = 4;

    private ServerSettings() {
        //
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        if(host != null && !"".equals(host)) {
            ServerSettings.host = host;
        }
    }

    public static Integer getPort() {
        return port;
    }

    public static void setPort(Integer port) {
        if(port != null) {
            ServerSettings.port = port;
        }
    }

    public static Integer getBufferSize() {
        return bufferSize;
    }

    public static void setBufferSize(Integer bufferSize) {
        ServerSettings.bufferSize = bufferSize;
    }

    public static Integer getBufferPoolInitialSize() {
        return bufferPoolInitialSize;
    }

    public static void setBufferPoolInitialSize(Integer bufferPoolInitialSize) {
        ServerSettings.bufferPoolInitialSize = bufferPoolInitialSize;
    }

    public static Integer getBufferPoolIncrement() {
        return bufferPoolIncrement;
    }

    public static void setBufferPoolIncrement(Integer bufferPoolIncrement) {
        ServerSettings.bufferPoolIncrement = bufferPoolIncrement;
    }

    public static Integer getNumberOfNioWorkers() {
        return numberOfNioWorkers;
    }

    public static void setNumberOfNioWorkers(Integer numberOfNioWorkers) {
        ServerSettings.numberOfNioWorkers = numberOfNioWorkers;
    }

    public static Integer getNumberOfNioReactors() {
        return numberOfNioReactors;
    }

    public static void setNumberOfNioReactors(Integer numberOfNioReactors) {
        ServerSettings.numberOfNioReactors = numberOfNioReactors;
    }
}
