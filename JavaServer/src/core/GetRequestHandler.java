package core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Handler for GET request.
 * Provides response containing number of unique words.
 */
public class GetRequestHandler implements HttpHandler {

    /**
     * Writes HTTP 200 response with number of unique words.
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String numberOfUniqueWords = String.valueOf(WordsHolder.INSTANCE.getNumberOfUniqueWords()) + "\r\n";
        WordsHolder.INSTANCE.clear();
        httpExchange.sendResponseHeaders(200, numberOfUniqueWords.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(numberOfUniqueWords.getBytes());
        outputStream.close();
    }
}
