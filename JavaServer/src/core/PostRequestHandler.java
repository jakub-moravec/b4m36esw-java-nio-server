package core;

import utils.HttpUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Handler for POST request.
 * Processes words in request content.
 */
public class PostRequestHandler {

    /**
     * Processes words in request content.
     * @return HTTP 200 response
     */
    public static String handleRequest(InputStream contentStream) {
        try {
            GZIPInputStream unzippedContentStream = new GZIPInputStream(contentStream);
            Set<String> words = parseWordsFromContentStream(unzippedContentStream);
            WordsHolder.getInstance().addWords(words);
            return HttpUtils.createHttpResponse(200, "OK", null);
        } catch (IOException e) {
            return HttpUtils.createHttpBadRequestResponse();
        }
    }

    /**
     * Reads POST content and parses words from it.
     * @param contentStream content stream
     * @return parsed words
     * @throws IOException in case of failure
     */
    private static Set<String> parseWordsFromContentStream(InputStream contentStream) throws IOException {
        Set<String> words = new HashSet<>();
        String word = "";
        int b;
        while (true) {
            try {
                if((b = contentStream.read()) >= 0) {
                    char c = (char) b;
                    if (c == ' ') {
                        words.add(word.trim());
                        word = "";
                    }
                    word += c;
                } else {
                    if(!"".equals(word)) {
                        words.add(word.trim());
                    }
                    break;
                }
            } catch (EOFException eofException) {
                if(!"".equals(word)) {
                    words.add(word.trim());
                }
                break;
            }
        }

        return  words;
    }
}
