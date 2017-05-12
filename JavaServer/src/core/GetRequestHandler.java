package core;

import utils.HttpUtils;

/**
 * Handler for GET request.
 * Provides response containing number of unique words.
 */
public class GetRequestHandler {

    /**
     * @return HTTP 200 response with number of unique words
     */
    public static String handleRequest() {
        int numberOfUniqueWords = WordsHolder.INSTANCE.getNumberOfUniqueWords();
        WordsHolder.INSTANCE.clear();
        return HttpUtils.createHttpResponse(200, "OK", String.valueOf(numberOfUniqueWords));
    }
}
