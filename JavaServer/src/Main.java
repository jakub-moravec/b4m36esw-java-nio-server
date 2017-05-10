import nioServer.NIOServer;
import settings.ServerSettings;

import java.io.IOException;

/**
 * Main class of Java server build upon Java nioServer.
 *
 * Used only for starting the server.
 */
public class Main {

    /**
     * Sets parameters & starts the server.
     * @param args no arguments are expected
     * @throws IOException in cace of communication failure
     */
    public static void main(String[] args) throws IOException {
        ServerSettings.setPort(1234); // FIXME: 10.5.17

        NIOServer.startNIOServer();
    }
}
