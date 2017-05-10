package NIO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOServer {
    final static int MSG_SIZE = 1000000;
    final static int BUFFER_POOL_SIZE = 30;
    private final static NIOReactor[] reactors;
    static ExecutorService workers = Executors.newFixedThreadPool(5);
    static BufferPool bufferPool = new BufferPool();

    static {
        reactors = new NIOReactor[4];
        try {
            for (int i = 0; i < reactors.length; i++) {
                reactors[i] = new NIOReactor();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startNIOServer(int port) throws IOException {
        reactors[0].register(NIOAcceptorHandler.getNIOAcceptorHandler(reactors, port));
    }

    static class BufferPool {
        private ConcurrentLinkedQueue<ByteBuffer> buffers = new ConcurrentLinkedQueue<>();

        public BufferPool() {
            addBuffers();
        }

        private void addBuffers(){
            for (int i = 0; i < BUFFER_POOL_SIZE; i++) {
                buffers.add(ByteBuffer.allocate(MSG_SIZE));
            }
        }

        public ByteBuffer getBuffer() {
            if(buffers.isEmpty()) {
               addBuffers();
            }
            return buffers.poll();
        }

        public void releaseBuffer(ByteBuffer buffer) {
            buffer.clear();
            buffers.add(buffer);
        }
    }
}