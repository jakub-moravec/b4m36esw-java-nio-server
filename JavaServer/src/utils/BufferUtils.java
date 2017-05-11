package utils;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

/**
 * Utility class helping with reading {@link ByteBuffer}.
 */
public class BufferUtils {

    /**
     * Reads one line from buffer.
     * Moves buffer position.
     * @param buffer buffer
     * @return read line
     */
    public static String readLine(ByteBuffer buffer) {
        String line = "";

        while(buffer.hasRemaining()) {
            char c = (char) buffer.get();
            switch (c) {
                case '\r':
                    //
                    break;
                case '\n':
                    return line;
                default:
                    line += c;
                    break;
            }

        }
        return line;
    }

    /**
     * Reads content from buffer starting at actual buffer position.
     * @param buffer buffer
     * @param contentLength number of bytes to read
     * @return read content
     */
    public static byte[] getContent(ByteBuffer buffer, int contentLength) {
        byte[] content = new byte[contentLength];
        int numberOfCharsRead = 0;
        for (int i = 0; i < contentLength; i++) {
            if(buffer.remaining() == 0) {
                break;
            }
            content[i] = buffer.get();
            numberOfCharsRead++;
        }

        byte[] result = new byte[numberOfCharsRead];
        System.arraycopy(content, 0, result, 0, numberOfCharsRead);

        return result;
    }
}
