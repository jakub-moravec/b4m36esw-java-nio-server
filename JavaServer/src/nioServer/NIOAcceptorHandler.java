package nioServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NIOAcceptorHandler extends NIOHandler {
    private final NIOReactor[] reactors;
    private final ServerSocketChannel channel;
    private int roundRobin = 0;

    static NIOAcceptorHandler getNIOAcceptorHandler(NIOReactor[] reactors, int port) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().bind(new InetSocketAddress(port));
        channel.configureBlocking(false);
        return new NIOAcceptorHandler(reactors, channel, SelectionKey.OP_ACCEPT);
    }


    public NIOAcceptorHandler(NIOReactor[] reactors, ServerSocketChannel channel, int selectableOps) {
        super(channel, selectableOps);
        this.reactors = reactors;
        this.channel = channel;
    }

    @Override
    public void run() {
        SocketChannel ch = null;
        try {
            ch = channel.accept();

            if (ch != null) {
                reactors[roundRobin].register(new NIOClientHandler(ch));
                roundRobin = (roundRobin + 1) % reactors.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

