package nioServer;

import core.GetRequestHandler;
import core.PostRequestHandler;
import model.HttpMethod;
import model.HttpRequestPath;
import utils.BufferUtils;
import utils.HttpUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NIOClientHandler extends NIOHandler {
    private final SocketChannel channel;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer = null;

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

    /**
     * Accepts request.
     * @throws IOException in case of failure
     */
    private void read() throws IOException {
        if (channel.read(readBuffer) == -1) {
            getSelectionKey().cancel();
            channel.close();
        } else if (readBuffer.remaining() != 0) {
            getSelectionKey().interestOps(0);
            getSelectionKey().selector().wakeup();
            NIOServer.workers.execute(this::process);
        }
    }

    /**
     * Reads the request, delegates request processing and sends response.
     */
    private void process() {
        // Read request headers
        readBuffer.flip();
        String httpHeadersFistLine = BufferUtils.readLine(readBuffer);
        String[] split = httpHeadersFistLine.split(" ");
        String httpMethod = split[0];
        String requestPath = split[1];

        List<String> httpHeaderLines = new ArrayList<>();
        String line;
        while (!"".equals(line = BufferUtils.readLine(readBuffer))) {
            httpHeaderLines.add(line);
        }

        Map<String, String> headers = HttpUtils.parseHttpHeaders(httpHeaderLines);

        // Delegate request
        String response;
        if (HttpRequestPath.COUNT.getName().equals(requestPath) && HttpMethod.GET.getName().equals(httpMethod)) {
            response = GetRequestHandler.handleRequest();
        } else if (HttpRequestPath.DATA.getName().equals(requestPath) && HttpMethod.POST.getName().equals(httpMethod)) {
            ByteArrayInputStream content = BufferUtils.getContent(readBuffer, Integer.valueOf(headers.get("Content-Length")));
            response = PostRequestHandler.handleRequest(content);
        } else {
            response = HttpUtils.createHttpBadRequestResponse();
        }

        // Send response
        writeBuffer = NIOServer.bufferPool.getBuffer();
        writeBuffer.put(response.getBytes());
        writeBuffer.flip();
        NIOServer.bufferPool.releaseBuffer(readBuffer);
        readBuffer = null;
        write(true);
    }

    /**
     * Sends response.
     * @param setWriteInterest write interest
     */
    private void write(boolean setWriteInterest) {
        try {
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
        } catch (IOException ioException) {
            readBuffer = writeBuffer;
            readBuffer.clear();
            writeBuffer = null;
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
