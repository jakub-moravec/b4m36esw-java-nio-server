import NIO.NIOServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        NIOServer.startNIOServer(1234);
    }
}
