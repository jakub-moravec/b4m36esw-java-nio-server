package nioServer;

import settings.ServerSettings;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Nio server itself.
 */
public class NIOServer {
    private final static int MSG_SIZE = ServerSettings.getBufferSize();
    private final static int BUFFER_POOL_SIZE = ServerSettings.getBufferPoolInitialSize();
    private final static int NUMBER_OF_NIO_WORKERS = ServerSettings.getNumberOfNioWorkers();
    private final static int NUMBER_OF_NIO_REACTORS = ServerSettings.getNumberOfNioReactors();

    private final static NIOReactor[] reactors;
    static ExecutorService workers = Executors.newFixedThreadPool(NUMBER_OF_NIO_WORKERS);
    static BufferPool bufferPool = new BufferPool();

    static {
        reactors = new NIOReactor[NUMBER_OF_NIO_REACTORS];
        try {
            for (int i = 0; i < reactors.length; i++) {
                reactors[i] = new NIOReactor();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startNIOServer() throws IOException {
        int port = ServerSettings.getPort();

        reactors[0].register(NIOAcceptorHandler.getNIOAcceptorHandler(reactors, port));
    }

    /**
     * Pool of read/write buffers.
     */
    static class BufferPool {
        private ConcurrentLinkedQueue<ByteBuffer> buffers = new ConcurrentLinkedQueue<>();

        BufferPool() {
            addBuffers(BUFFER_POOL_SIZE);
        }

        private void addBuffers(int n){
            for (int i = 0; i < n; i++) {
                buffers.add(ByteBuffer.allocate(MSG_SIZE));
            }
        }

        /**
         * Provides next available buffer. If no buffers are available, the pool is enlarged.
         * @return buffer
         */
        ByteBuffer getBuffer() {
            if(buffers.isEmpty()) {
               addBuffers(ServerSettings.getBufferPoolIncrement());
            }
            return buffers.poll();
        }

        /**
         * Releases buffer and returns it to the pool.
         * @param buffer buffer
         */
        void releaseBuffer(ByteBuffer buffer) {
            buffer.clear();
            buffers.add(buffer);
        }
    }
}