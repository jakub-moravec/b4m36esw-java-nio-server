package nioServer;

import core.GetRequestHandler;
import model.HttpRequest;
import core.PostRequestHandler;
import model.HttpMethod;
import utils.BufferUtils;
import utils.HttpUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NIOClientHandler extends NIOHandler {
    private final SocketChannel socket;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer = null;
    private HttpRequest httpRequest;

    NIOClientHandler(SocketChannel ch) throws IOException {
        super(ch, SelectionKey.OP_READ);
        this.socket = ch;
        readBuffer = NIOServer.bufferPool.getBuffer();
        socket.configureBlocking(false);
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
        if (socket.read(readBuffer) == -1) {
            getSelectionKey().cancel();
            socket.close();
        } else { //if (readBuffer.remaining() != 0) { FIXME
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

        if(httpRequest == null) {
            String httpMethod  = BufferUtils.readLine(readBuffer).split(" ")[0];

            List<String> httpHeaderLines = new ArrayList<>();
            String line;
            while (!"".equals(line = BufferUtils.readLine(readBuffer))) {
                httpHeaderLines.add(line);
            }

            httpRequest = new HttpRequest(HttpUtils.parseHttpHeaders(httpHeaderLines), HttpMethod.valueOf(httpMethod));
        }

        // Delegate request
        String response;
        switch (httpRequest.getHttpMethod()) {
            case GET:
                response = GetRequestHandler.handleRequest();
                break;
            case POST:
                byte[] contentFragment = BufferUtils.getContent(readBuffer, httpRequest.getMissingBytes());
                int missingBytes = httpRequest.addContent(contentFragment);
                if(missingBytes == 0) {
                    response = PostRequestHandler.handleRequest(new ByteArrayInputStream(httpRequest.getContent()));
                    break;
                } else {
                    readBuffer.clear();
                    getSelectionKey().interestOps(SelectionKey.OP_READ);
                    getSelectionKey().selector().wakeup();
                    return;
                }
            default:
                response = HttpUtils.createHttpBadRequestResponse();
                break;
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
            if (socket.write(writeBuffer) == -1) {
                getSelectionKey().cancel();
                socket.close();
            } else if (writeBuffer.remaining() > 0) {
                if (setWriteInterest) {
                    getSelectionKey().interestOps(SelectionKey.OP_WRITE);
                }
            } else {
                writeBuffer.clear();
                writeBuffer = null;
                getSelectionKey().interestOps(SelectionKey.OP_READ);
                socket.close();
            }
            if (setWriteInterest) {
                getSelectionKey().selector().wakeup();
            }
        } catch (IOException ioException) {
            readBuffer = writeBuffer;
            readBuffer.clear();
            writeBuffer = null;
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
