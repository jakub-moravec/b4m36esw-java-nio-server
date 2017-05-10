package utils;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

/**
 * Created by james on 10.5.17.
 */
public class BufferUtils {

    public static String readLine(ByteBuffer buffer, int position) {
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

    public static ByteArrayInputStream getContent(ByteBuffer buffer, int contentLenght) {
        byte[] content = new byte[contentLenght];
        for (int i = 0; i < contentLenght; i++) {
            content[i] = buffer.get();
        }
        return new ByteArrayInputStream(content);
    }
}
