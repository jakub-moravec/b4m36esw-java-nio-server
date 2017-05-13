import com.sun.net.httpserver.HttpServer;
import core.GetRequestHandler;
import core.PostRequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class of Java server build upon Java nioServer.
 *
 * Used only for starting the server.
 */
public class Main {

    private static int port = 7777;
    private static final int BACKLOG = 0;
    private static final String POST_URL = "/esw/myserver/data";
    private static final String GET_URL = "/esw/myserver/count";

    /**
     * Sets parameters & starts the server.
     * @param args if first argument is present, it is taken as port number
     * @throws IOException in cace of communication failure
     */
    public static void main(String[] args) throws IOException {
        if(args.length == 1) {
            port = Integer.valueOf(args[0]);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), BACKLOG);
        server.createContext(POST_URL, new PostRequestHandler());
        server.createContext(GET_URL, new GetRequestHandler());

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        server.setExecutor(executorService);
        server.start();
    }
}
