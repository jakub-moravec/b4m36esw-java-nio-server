package core;

import utils.HttpUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
            List<String> words = parseWordsFromContentStream(unzippedContentStream);
            WordsHolder.INSTANCE.addWords(words);
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
