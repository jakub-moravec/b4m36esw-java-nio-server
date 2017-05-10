package NIO;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import utils.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class NIOClientHandler extends NIOHandler {
    private final SocketChannel channel;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer = null;
    private GZIPInputStream gzipInputStream;

    public NIOClientHandler(SocketChannel ch) throws IOException {
        super(ch, SelectionKey.OP_READ);
        this.channel = ch;
        readBuffer = NIOServer.bufferPool.getBuffer();
        channel.configureBlocking(false);
    }

    @Override
    public void run() {
        try {
            if (getSelectionKey().isReadable()) {
                read();
            } else if (getSelectionKey().isWritable()) {
                write(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read() throws IOException {
        if (channel.read(readBuffer) == -1) {
            getSelectionKey().cancel();
            channel.close();
        } else { //if (readBuffer.remaining() != 0) {
            getSelectionKey().interestOps(0);
            getSelectionKey().selector().wakeup();
            NIOServer.workers.execute(this::process);
        }
    }

    private void process() {
        Map<String, String> headers = new HashMap<>();

        try {
            readBuffer.flip();

            String[] line;

            BufferUtils.readLine(readBuffer, 0); // request header
            BufferUtils.readLine(readBuffer, 0); // Host
            BufferUtils.readLine(readBuffer, 0); // User-Agent
            BufferUtils.readLine(readBuffer, 0); // Accept
            line = BufferUtils.readLine(readBuffer, 0).split(": "); // Content-Length
            headers.put(line[0], line[1]);
            BufferUtils.readLine(readBuffer, 0); // Content-Type
            readBuffer.get(); // '\r'
            readBuffer.get(); // '\n'

            int contentStartPosition = readBuffer.position();

            ByteArrayInputStream content = BufferUtils.getContent(readBuffer, Integer.valueOf(headers.get("Content-Length")) - 1);


            gzipInputStream = new GZIPInputStream(content);

            while (gzipInputStream.available() > 0) {
                System.out.print((char) gzipInputStream.read());
            }



            writeBuffer = NIOServer.bufferPool.getBuffer();

            writeBuffer.put(readBuffer);
            writeBuffer.flip();
            NIOServer.bufferPool.releaseBuffer(readBuffer);
            readBuffer = null;
            write(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(boolean setWriteInterest) throws IOException {
        if (channel.write(writeBuffer) == -1) {
            getSelectionKey().cancel();
            channel.close();
        } else if (writeBuffer.remaining() > 0) {
            if (setWriteInterest) {
                getSelectionKey().interestOps(SelectionKey.OP_WRITE);
            }
        } else {
            readBuffer = writeBuffer;
            readBuffer.clear();
            writeBuffer = null;
            getSelectionKey().interestOps(SelectionKey.OP_READ);
            channel.close();
        }
        if (setWriteInterest) {
            getSelectionKey().selector().wakeup();
        }
    }
}
