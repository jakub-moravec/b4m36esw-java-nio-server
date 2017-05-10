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

            String[] line = new String[2];

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


//            System.out.println(readBuffer.array());
//
//            System.out.println(readBuffer.arrayOffset());
//
//            char c = ' ';
//            String key = "", value = "";
//            while (readBuffer.hasRemaining()) { // && (c = (char) readBuffer.get()) != '\n') {
//
//                boolean charIsKey = true;
//                if(c == ':') {
//                    charIsKey = false;
//                }
//
//                if(charIsKey) {
//                    key += c;
//                } else {
//                    value += c;
//                }
//
//
//            }
//            headers.put(key, value);

//
//
//            for (int i = 0; i < readBuffer.array().length; i++) {
//
//            }
//            String x = "";
//            for (byte b : readBuffer.array()) {
//                System.out.print((char) b);
//                x += (char) b;
//            }
//            System.out.println(x);

//

//            System.out.println("aaaaaaaaaaaaa");
//            BASE64Decoder decoder = new BASE64Decoder();
//            byte[] decodedBytes = decoder.decodeBuffer(readBuffer);

//            BASE64Encoder encoder = new BASE64Encoder();
//            byte[] encodedBytes = encoder.encodeBuffer(new ByteArrayInputStream(readBuffer.array()),);
//
//            BufferedReader reader = new BufferedReader(new Reader(readBuffer));

            gzipInputStream = new GZIPInputStream(content);

//            while (readBuffer.hasRemaining()) {
//                System.out.print((char) readBuffer.get());
//            }
//
//            System.out.println("bbbbbbbbbbbbb");


            
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
