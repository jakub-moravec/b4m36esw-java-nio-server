package NIO;

import utils.BufferUtils;
import utils.HttpHeadersUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class NIOClientHandler extends NIOHandler {
    private final SocketChannel channel;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer = null;
    private GZIPInputStream gzipInputStream;

    NIOClientHandler(SocketChannel ch) throws IOException {
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
        try {
            readBuffer.flip();
            String httpHeadersFistLine = BufferUtils.readLine(readBuffer);
            String[] split = httpHeadersFistLine.split(" ");
            String httpMethod = split[0];
            String host = split[1];

            List<String> httpHeaderLines = new ArrayList<>();
            String line;
            while (!"".equals(line = BufferUtils.readLine(readBuffer))) {
                httpHeaderLines.add(line);
            }

            Map<String, String> headers = HttpHeadersUtils.buildHttpHeaders(httpHeaderLines);

            ByteArrayInputStream content = BufferUtils.getContent(readBuffer, Integer.valueOf(headers.get("Content-Length")) - 1);


            gzipInputStream = new GZIPInputStream(content);

            List<String> words = new ArrayList<>();
            String word = "";
            int b;
            while (true) {
                try {
                    if((b = gzipInputStream.read()) >= 0) {
                        char c = (char) b;
                        if (c == ' ') {
                            words.add(word);
                            word = "";
                        }
                        word += c;
                    }
                } catch (EOFException eofException) {
                    if(!"".equals(word)) {
                        words.add(word);
                    }
                    break;
                }
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
