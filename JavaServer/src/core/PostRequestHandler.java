package core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(unzippedContentStream));
//            StringBuilder sb = new StringBuilder();
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                sb.append(line);
//            }

            List<String> words = parseWordsFromContentStream(unzippedContentStream);
//            WordsHolder.INSTANCE.addWords(sb.toString());
            WordsHolder.INSTANCE.addWords(words);
            httpExchange.sendResponseHeaders(204, -1);
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(500, 0);
        }
    }

    /**
     * Reads POST content and parses words from it.
     * @param contentStream content stream
     * @return parsed words
     * @throws IOException in case of failure
     */
    private static List<String> parseWordsFromContentStream(InputStream contentStream) throws IOException {
        List<String> words = new ArrayList<String>();
        String word = "";
        int b;
        while (true) {
            try {
                if((b = contentStream.read()) >= 0) {
                    char c = (char) b;
                    if (c == ' ') {
                        addWord(words, word);
                        word = "";
                    }
                    word += c;
                } else {
                    addWord(words, word);
                    break;
                }
            } catch (EOFException eofException) {
                addWord(words, word);
                break;
            }
        }
        contentStream.close();
        return  words;
    }

    private static void addWord(List<String> words, String word) {
        word = word.trim();
        if(!"".equals(word)) {
            words.add(word);
        }
    }
}
