package core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * Handler for POST request.
 * Processes words in request content.
 */
public class PostRequestHandler implements HttpHandler {

    /**
     * Processes words in request content.
     * Writes HTTP 204 response.
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException  {
        try {
            GZIPInputStream unzippedContentStream = new GZIPInputStream(httpExchange.getRequestBody());
            parseWordsFromContentStream(unzippedContentStream);
            httpExchange.sendResponseHeaders(204, -1);
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(500, 0);
        }
    }

    /**
     * Reads POST content and parses words from it.
     * @param contentStream content stream
     * @throws IOException in case of failure
     */
    private static void parseWordsFromContentStream(InputStream contentStream) throws IOException {
        BufferedReader br = new BufferedReader( new InputStreamReader(contentStream));
        String line = null;
        while ((line = br.readLine()) != null) {
            for (String word : line.split("\\s+")) {
                WordsHolder.INSTANCE.addWord(word);
            }
        }
        contentStream.close();
    }
}
